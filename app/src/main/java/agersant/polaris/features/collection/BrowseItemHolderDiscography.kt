package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class BrowseItemHolderDiscography(
    private val api: API,
    playbackQueue: PlaybackQueue,
    adapter: BrowseAdapter,
    itemView: View,
    itemQueueStatusView: View,
) : BrowseItemHolder(api, playbackQueue, adapter, itemView, itemQueueStatusView, itemQueueStatusView) { //TODO use different status view for queue next

    private val artwork: ImageView = itemView.findViewById(R.id.artwork)
    private val artist: TextView = itemView.findViewById(R.id.artist)
    private val album: TextView = itemView.findViewById(R.id.album)

    init {
        itemView.setOnClickListener(this)
    }

    override fun bind(item: CollectionItem) {
        super.bind(item)
        val artistValue = item.artist
        if (artistValue != null) {
            artist.text = artistValue
        }
        val albumValue = item.album
        if (albumValue != null) {
            album.text = albumValue
        }
        api.loadImageIntoView(item, artwork)
    }
}