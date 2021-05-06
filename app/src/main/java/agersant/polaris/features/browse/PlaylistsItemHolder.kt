package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.Playlist
import agersant.polaris.R
import agersant.polaris.api.API
import agersant.polaris.api.ItemsCallback
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

internal class PlaylistsItemHolder(
    itemView: View,
    private val queueStatusView: View,
    private val api: API,
    private val playbackQueue: PlaybackQueue,
    private val adapter: PlaylistsAdapter,
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var item: Playlist? = null
    private val nameText: TextView = itemView.findViewById(R.id.name)
    private val queueStatusText: TextView = queueStatusView.findViewById(R.id.status_text)
    private val queueStatusIcon: ImageView = queueStatusView.findViewById(R.id.status_icon)

    init {
        itemView.setOnClickListener(this)
    }

    fun bindItem(item: Playlist) {
        this.item = item
        setStatusToIdle()

        nameText.text = item.name
    }

    override fun onClick(view: View) {
        item?.let {
            val args = Bundle()
            args.putSerializable(PlaylistFragment.PLAYLIST, it)
            view.findNavController().navigate(R.id.nav_playlist, args)
        }
    }

    fun onSwiped(view: View?) {
        queuePlaylist()
        setStatusToFetching()
    }

    private fun queuePlaylist() {
        val fetchingItem = item
        val handlers = object : ItemsCallback {
            override fun onSuccess(items: List<CollectionItem>) {
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
        api.getPlaylist(item!!.name, handlers)
    }

    private fun setStatusToIdle() {
        queueStatusText.setText(R.string.add_to_queue)
        queueStatusIcon.setImageResource(R.drawable.ic_playlist_play_24)
        itemView.requestLayout()
    }

    private fun setStatusToFetching() {
        queueStatusText.setText(R.string.queuing)
        queueStatusIcon.setImageResource(R.drawable.ic_hourglass_empty_24)
        itemView.requestLayout()
    }

    private fun setStatusToQueued() {
        queueStatusText.setText(R.string.queued)
        queueStatusIcon.setImageResource(R.drawable.ic_check_24)
        itemView.requestLayout()
        waitAndSwipeBack()
    }

    private fun setStatusToQueueError() {
        queueStatusText.setText(R.string.queuing_error)
        queueStatusIcon.setImageResource(R.drawable.ic_error_24)
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
        }, 1000)
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
