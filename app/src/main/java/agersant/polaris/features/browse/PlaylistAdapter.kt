package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.api.API
import agersant.polaris.databinding.ViewPlaylistItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

internal class PlaylistAdapter(
    private val api: API,
    private val playbackQueue: PlaybackQueue,
) : RecyclerView.Adapter<PlaylistItemHolder>() {

    inner class DiffCallback : DiffUtil.Callback() {
        var oldItems = items

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = items.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].name == items[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == items[newItemPosition]
        }
    }

    private val diffCallback = DiffCallback()

    var items: List<CollectionItem> = listOf()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewPlaylistItemBinding.inflate(inflater, parent, false)
        return PlaylistItemHolder(binding, api, playbackQueue)
    }

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<CollectionItem>) {
        diffCallback.oldItems = this.items
        this.items = items
        val diff = DiffUtil.calculateDiff(diffCallback)
        diff.dispatchUpdatesTo(this)
    }
}
