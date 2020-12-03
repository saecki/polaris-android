package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import agersant.polaris.api.ItemsCallback
import agersant.polaris.features.collection.directories.DirectoriesFragment
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.os.postDelayed
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.math.ceil

abstract class BrowseItemHolder(
    private val api: API,
    private val playbackQueue: PlaybackQueue,
    private val adapter: BrowseAdapter,
    itemView: View,
    private val queueStatusView: View,
    private val queueNextStatusView: View,
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var item: CollectionItem

    open fun bind(item: CollectionItem) {
        this.item = item
        setStatusToIdle(queueStatusView)
        setStatusToIdle(queueNextStatusView)
    }

    override fun onClick(view: View) {
        if (item.isDirectory) {
            val navController = Navigation.findNavController(itemView)
            val bundle = Bundle()
            bundle.putString(DirectoriesFragment.PATH, item.path)
            navController.navigate(R.id.nav_directories, bundle)
        }
    }

    open fun onSwiped(view: View?, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            if (item.isDirectory) {
                queueDirectory(queueNextStatusView) //TODO: queue next
                setStatusToFetching(queueNextStatusView)
            } else {
                playbackQueue.add(item) //TODO: queue next
                setStatusToQueued(queueNextStatusView)
            }
        } else {
            if (item.isDirectory) {
                queueDirectory(queueStatusView)
                setStatusToFetching(queueStatusView)
            } else {
                playbackQueue.add(item)
                setStatusToQueued(queueStatusView)
            }
        }
    }

    private fun queueDirectory(view: View) {
        val fetchingItem = item
        val handlers: ItemsCallback = object : ItemsCallback {
            override fun onSuccess(items: ArrayList<out CollectionItem>) {
                Handler(Looper.getMainLooper()).post {
                    playbackQueue.addAll(items)
                    if (item === fetchingItem) {
                        setStatusToQueued(view)
                    }
                }
            }

            override fun onError() {
                Handler(Looper.getMainLooper()).post {
                    if (item === fetchingItem) {
                        setStatusToQueueError(view)
                    }
                }
            }
        }
        api.flatten(item.path, handlers)
    }

    private fun setStatusToIdle(view: View) {
        val statusText = view.findViewById<MaterialTextView>(R.id.status_text)
        val statusIcon = view.findViewById<ImageView>(R.id.status_icon)
        statusText.setText(R.string.add_to_queue)
        statusIcon.setImageResource(R.drawable.baseline_playlist_add_24)
        view.requestLayout()
    }

    private fun setStatusToFetching(view: View) {
        val statusText = view.findViewById<MaterialTextView>(R.id.status_text)
        val statusIcon = view.findViewById<ImageView>(R.id.status_icon)
        statusText.setText(R.string.queuing)
        statusIcon.setImageResource(R.drawable.baseline_hourglass_empty_24)
        view.requestLayout()
    }

    private fun setStatusToQueued(view: View) {
        val statusText = view.findViewById<MaterialTextView>(R.id.status_text)
        val statusIcon = view.findViewById<ImageView>(R.id.status_icon)
        statusText.setText(R.string.queued)
        statusIcon.setImageResource(R.drawable.baseline_playlist_add_check_24)
        view.requestLayout()
        waitAndSwipeBack()
    }

    private fun setStatusToQueueError(view: View) {
        val statusText = view.findViewById<MaterialTextView>(R.id.status_text)
        val statusIcon = view.findViewById<ImageView>(R.id.status_icon)
        statusText.setText(R.string.queuing_error)
        statusIcon.setImageResource(R.drawable.baseline_error_24)
        view.requestLayout()
        waitAndSwipeBack()
    }

    private fun waitAndSwipeBack() {
        val oldItem = item
        val handler = Handler()
        handler.postDelayed(1000L) {
            if (item === oldItem) {
                val position = adapterPosition
                adapter.notifyItemChanged(position)
            }
        }
    }

    fun onChildDraw(canvas: Canvas, dX: Float, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(itemView.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(itemView.height, View.MeasureSpec.EXACTLY)

            if (dX > 0) {
                queueStatusView.measure(widthSpec, heightSpec)
                queueStatusView.layout(0, 0, queueStatusView.measuredWidth, queueStatusView.measuredHeight)
                canvas.save()
                canvas.translate(itemView.left.toFloat(), itemView.top.toFloat())

                val clipWidth = ceil(dX).toInt()
                canvas.clipRect(0, 0, clipWidth, queueStatusView.measuredHeight)

                queueStatusView.draw(canvas)
                canvas.restore()
            } else {
                queueNextStatusView.measure(widthSpec, heightSpec)
                queueNextStatusView.layout(0, 0, queueNextStatusView.measuredWidth, queueNextStatusView.measuredHeight)
                canvas.save()
                canvas.translate(itemView.left.toFloat(), itemView.top.toFloat())

                val clipWidth = ceil(dX).toInt()
                canvas.clipRect(itemView.width - clipWidth, 0, clipWidth, queueNextStatusView.measuredHeight)

                queueNextStatusView.draw(canvas)
                canvas.restore()
            }
        }
    }

}