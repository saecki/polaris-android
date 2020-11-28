package agersant.polaris.features.queue

import agersant.polaris.PolarisState
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class QueueAdapter(
    private val state: PolarisState,
) : RecyclerView.Adapter<QueueItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueItemHolder {
        val view = QueueItemView(parent.context)
        return QueueItemHolder(state, view)
    }

    override fun onBindViewHolder(holder: QueueItemHolder, position: Int) {
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