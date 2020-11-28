package agersant.polaris.ui

import androidx.recyclerview.widget.DiffUtil

abstract class ListDiffUtil<T>(protected val old: List<T>, protected val new: List<T>) : DiffUtil.Callback() {

    interface Factory<T> {
        fun create(old: List<T>, new: List<T>): ListDiffUtil<T>
    }

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size
}