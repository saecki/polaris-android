package agersant.polaris.features.queue

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisState
import agersant.polaris.ui.CollectionItemDiffUtil
import agersant.polaris.ui.OverscrollAdapter
import android.view.ViewGroup

internal class QueueAdapter(
    private val state: PolarisState,
) : OverscrollAdapter<CollectionItem, QueueItemHolder>(CollectionItemDiffUtil.Factory()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueItemHolder {
        val view = QueueItemView(parent.context)
        return QueueItemHolder(state, view)
    }

    override fun onBindViewHolder(holder: QueueItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(state.playbackQueue.getItem(position))
    }

    override fun getItemCount(): Int {
        return state.playbackQueue.size()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        state.playbackQueue.swap(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onItemDismiss(position: Int) {
        state.playbackQueue.remove(position)
        notifyItemRemoved(position)
    }
}