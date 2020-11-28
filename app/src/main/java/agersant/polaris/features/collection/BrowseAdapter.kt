package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.ui.CollectionItemDiffUtil
import agersant.polaris.ui.OverscrollAdapter

abstract class BrowseAdapter() : OverscrollAdapter<CollectionItem, BrowseItemHolder>(CollectionItemDiffUtil.Factory()) {

    override fun onBindViewHolder(holder: BrowseItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}