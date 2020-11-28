package agersant.polaris.ui

import androidx.annotation.CallSuper
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class OverscrollAdapter<T, V : RecyclerView.ViewHolder>(
    private val listDiffUtilFactory: ListDiffUtil.Factory<T>,
) : RecyclerView.Adapter<V>() {

    var topPadding = 0
    var bottomPadding = 0

    open var items: List<T> = listOf()
        set(value) {
            val diff = DiffUtil.calculateDiff(listDiffUtilFactory.create(field, value))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    @CallSuper
    override fun onBindViewHolder(holder: V, position: Int) {
        if (position == 0 && items.lastIndex == 0) {
            holder.itemView.updatePadding(top = topPadding, bottom = bottomPadding)
        } else if (position == 0) {
            holder.itemView.updatePadding(top = topPadding, bottom = 0)
        } else if (position == items.lastIndex) {
            holder.itemView.updatePadding(top = 0, bottom = bottomPadding)
        } else {
            holder.itemView.updatePadding(top = 0, bottom = 0)
        }
    }

    override fun getItemCount(): Int = items.size
}
