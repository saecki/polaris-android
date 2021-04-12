package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.Song
import agersant.polaris.api.API
import agersant.polaris.api.ThumbnailSize
import agersant.polaris.databinding.ViewBrowseAlbumBinding
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import java.util.*

@SuppressLint("ViewConstructor")
private class BrowseViewAlbum(
    context: Context,
    private val api: API,
    playbackQueue: PlaybackQueue,
) : BrowseViewContent(context) {

    private val adapter: BrowseAdapter
    private val artwork: ImageView
    private val artist: TextView
    private val title: TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ViewBrowseAlbumBinding.inflate(inflater, this, true)
        artwork = binding.albumArtwork
        artist = binding.albumArtist
        title = binding.albumTitle

        val recyclerView = binding.browseRecyclerView
        recyclerView.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = BrowseTouchCallback()
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter = BrowseAdapterAlbum(api, playbackQueue)
        recyclerView.adapter = adapter
    }

    override fun setItems(items: List<CollectionItem>) {
        val songs = items.filterIsInstance<Song>().sortedWith { a, b ->
            val discDifference = a.discNumber - b.discNumber
            if (discDifference != 0) {
                discDifference
            } else {
                a.trackNumber - b.trackNumber
            }
        }
        adapter.setItems(songs)
        val song = songs.first()

        var artistString = song.albumArtist ?: song.artist ?: ""
        if (song.year != -1) {
            artistString += " • ${song.year}"
        }
        artist.text = artistString
        title.text = song.album ?: ""
        if (song.artwork != null) {
            api.loadThumbnailIntoView(song, ThumbnailSize.Small, artwork)
        } else {
            artwork.setImageResource(R.drawable.ic_fallback_artwork)
        }
    }
}
