package agersant.polaris.adapter

import agersant.polaris.CollectionItem
import androidx.recyclerview.widget.DiffUtil

abstract class ListDiffUtil<T>(protected val old: List<T>, protected val new: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size
}

class CollectionItemDiffUtil(old: List<CollectionItem>, new: List<CollectionItem>) : ListDiffUtil<CollectionItem>(old, new) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].path == new[newItemPosition].path
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}
