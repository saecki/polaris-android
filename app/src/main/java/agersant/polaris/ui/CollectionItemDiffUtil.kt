package agersant.polaris.ui

import agersant.polaris.CollectionItem


class CollectionItemDiffUtil(old: List<CollectionItem>, new: List<CollectionItem>) : ListDiffUtil<CollectionItem>(old, new) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].path == new[newItemPosition].path
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}