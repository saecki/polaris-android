package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.ui.CollectionItemDiffUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BrowseAdapter() : RecyclerView.Adapter<BrowseItemHolder>() {

    open var items: List<CollectionItem> = listOf()
        set(value) {
            val diff = DiffUtil.calculateDiff(CollectionItemDiffUtil(field, value))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    override fun onBindViewHolder(holder: BrowseItemHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}