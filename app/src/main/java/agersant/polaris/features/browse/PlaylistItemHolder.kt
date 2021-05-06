package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import agersant.polaris.api.ThumbnailSize
import agersant.polaris.databinding.ViewPlaylistItemBinding
import androidx.recyclerview.widget.RecyclerView

class PlaylistItemHolder(
    binding: ViewPlaylistItemBinding,
    private val api: API,
    private val playbackQueue: PlaybackQueue,
) : RecyclerView.ViewHolder(binding.root) {

    val artwork = binding.artwork
    val title = binding.title
    val artist = binding.artist

    var item: CollectionItem? = null

    fun bindItem(item: CollectionItem) {
        title.text = item.title.orEmpty()
        artist.text = item.artist.orEmpty()

        if (item.artwork != null) {
            api.loadThumbnailIntoView(item, ThumbnailSize.Small, artwork)
        } else {
            artwork.setImageResource(R.drawable.ic_fallback_artwork)
        }
    }
}
