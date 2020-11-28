package agersant.polaris.features.collection

import agersant.polaris.App
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
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var item: CollectionItem
    private val statusText: MaterialTextView = queueStatusView.findViewById(R.id.status_text)
    private val statusIcon: ImageView = queueStatusView.findViewById(R.id.status_icon)

    open fun bind(item: CollectionItem) {
        this.item = item
        setStatusToIdle()
    }

    override fun onClick(view: View) {
        if (item.isDirectory) {
            val navController = Navigation.findNavController(itemView)
            val bundle = Bundle()
            bundle.putString(DirectoriesFragment.PATH, item.path)
            navController.navigate(R.id.nav_directories, bundle)
        }
    }

    open fun onSwiped(view: View?) {
        if (item.isDirectory) {
            queueDirectory()
            setStatusToFetching()
        } else {
            playbackQueue.addItem(item)
            setStatusToQueued()
        }
    }

    private fun queueDirectory() {
        val fetchingItem = item
        val handlers: ItemsCallback = object : ItemsCallback {
            override fun onSuccess(items: ArrayList<out CollectionItem>) {
                Handler(Looper.getMainLooper()).post {
                    playbackQueue.addItems(items)
                    if (item === fetchingItem) {
                        setStatusToQueued()
                    }
                }
            }

            override fun onError() {
                Handler(Looper.getMainLooper()).post {
                    if (item === fetchingItem) {
                        setStatusToQueueError()
                    }
                }
            }
        }
        api.flatten(item.path, handlers)
    }

    private fun setStatusToIdle() {
        statusText.setText(R.string.add_to_queue)
        statusIcon.setImageResource(R.drawable.ic_playlist_play_black_24dp)
        itemView.requestLayout()
    }

    private fun setStatusToFetching() {
        statusText.setText(R.string.queuing)
        statusIcon.setImageResource(R.drawable.ic_hourglass_empty_black_24dp)
        itemView.requestLayout()
    }

    private fun setStatusToQueued() {
        statusText.setText(R.string.queued)
        statusIcon.setImageResource(R.drawable.ic_check_black_24dp)
        itemView.requestLayout()
        waitAndSwipeBack()
    }

    private fun setStatusToQueueError() {
        statusText.setText(R.string.queuing_error)
        statusIcon.setImageResource(R.drawable.ic_error_black_24dp)
        itemView.requestLayout()
        waitAndSwipeBack()
    }

    private fun waitAndSwipeBack() {
        val oldItem = item
        val handler = Handler()
        handler.postDelayed({
            if (item === oldItem) {
                val position = adapterPosition
                adapter.notifyItemChanged(position)
            }
        }, App.resources.getInteger(android.R.integer.config_longAnimTime).toLong())
    }

    fun onChildDraw(canvas: Canvas, dX: Float, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(itemView.width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(itemView.height, View.MeasureSpec.EXACTLY)
            queueStatusView.measure(widthSpec, heightSpec)
            queueStatusView.layout(0, 0, queueStatusView.measuredWidth, queueStatusView.measuredHeight)
            canvas.save()
            canvas.translate(itemView.left.toFloat(), itemView.top.toFloat())
            canvas.clipRect(0, 0, ceil(dX.toDouble()).toInt(), queueStatusView.measuredHeight)
            queueStatusView.draw(canvas)
            canvas.restore()
        }
    }

}