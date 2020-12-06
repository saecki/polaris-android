package agersant.polaris.features.queue

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisState
import agersant.polaris.R
import agersant.polaris.adapter.CollectionItemDiffUtil
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class QueueAdapter(
    private val state: PolarisState,
    private var items: List<CollectionItem>,
) : RecyclerView.Adapter<QueueItemHolder>() {

    var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueItemHolder {
        val view = QueueItemView(parent.context)
        return QueueItemHolder(state, itemTouchHelper, view)
    }

    override fun onBindViewHolder(holder: QueueItemHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        state.playbackQueue.swap(fromPosition, toPosition)
    }

    fun onItemDismiss(position: Int) {
        state.playbackQueue.removeAt(position)
    }

    fun updateItems(items: List<CollectionItem>) {
        val diff = DiffUtil.calculateDiff(CollectionItemDiffUtil(this.items, items))
        this.items = items
        diff.dispatchUpdatesTo(this)
    }
}