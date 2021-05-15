package agersant.polaris.features.playlist

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import agersant.polaris.api.ThumbnailSize
import agersant.polaris.databinding.ViewPlaylistItemBinding
import agersant.polaris.databinding.ViewQueueStatusBinding
import agersant.polaris.features.SwipeableHolder
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

internal class PlaylistItemHolder(
    private val adapter: PlaylistAdapter,
    private val api: API,
    private val playbackQueue: PlaybackQueue,
    itemBinding: ViewPlaylistItemBinding,
    queueStatusBinding: ViewQueueStatusBinding,
) : RecyclerView.ViewHolder(itemBinding.root), SwipeableHolder {

    private val artwork = itemBinding.artwork
    private val title = itemBinding.title
    private val artist = itemBinding.artist
    private val queueStatusView = queueStatusBinding.root
    private val queueStatusText = queueStatusBinding.statusText
    private val queueStatusIcon = queueStatusBinding.statusIcon

    private var item: CollectionItem? = null

    fun bindItem(item: CollectionItem) {
        this.item = item
        title.text = item.title.orEmpty()
        artist.text = item.artist.orEmpty()
        if (item.artwork != null) {
            api.loadThumbnailIntoView(item, ThumbnailSize.Small, artwork)
        } else {
            artwork.setImageResource(R.drawable.ic_fallback_artwork)
        }

        setStatusToIdle()
    }

    override fun onSwiped(direction: Int) {
        item?.let(playbackQueue::addItem)
        setStatusToQueued()
    }

    private fun setStatusToIdle() {
        queueStatusText.setText(R.string.add_to_queue)
        queueStatusIcon.setImageResource(R.drawable.ic_playlist_play_24)
        itemView.requestLayout()
    }

    private fun setStatusToQueued() {
        queueStatusText.setText(R.string.queued)
        queueStatusIcon.setImageResource(R.drawable.ic_check_24)
        itemView.requestLayout()
        waitAndSwipeBack()
    }

    private fun waitAndSwipeBack() {
        val oldItem = item
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(1000) {
            if (item === oldItem) {
                val position = adapterPosition
                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun onChildDraw(canvas: Canvas, dX: Float, actionState: Int) {
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
