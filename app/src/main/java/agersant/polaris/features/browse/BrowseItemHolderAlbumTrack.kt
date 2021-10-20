package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.view.View
import android.widget.TextView

internal class BrowseItemHolderAlbumTrack(
    api: API?,
    playbackQueue: PlaybackQueue?,
    adapter: BrowseAdapter?,
    itemView: View,
    itemQueueStatusView: View?
) : BrowseItemHolder(api, playbackQueue, adapter, itemView, itemQueueStatusView) {

    private val trackNumberText: TextView = itemView.findViewById(R.id.track_number)
    private val titleText: TextView = itemView.findViewById(R.id.title)
    private val artistText: TextView = itemView.findViewById(R.id.artist)

    override fun bindItem(item: CollectionItem) {
        super.bindItem(item)

        val trackNumber = item.trackNumber
        if (trackNumber != -1) {
            trackNumberText.text = String.format("%02d.", trackNumber)
        } else {
            trackNumberText.text = ""
        }
        titleText.text = item.title ?: item.name
        artistText.text = item.artist ?: ""
    }

}
