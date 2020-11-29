package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.view.View
import android.widget.TextView
import java.util.*

internal class BrowseItemHolderAlbumTrack(
    api: API,
    playbackQueue: PlaybackQueue,
    adapter: BrowseAdapter,
    itemView: View,
    itemQueueStatusView: View,
) : BrowseItemHolder(api, playbackQueue, adapter, itemView, itemQueueStatusView, itemQueueStatusView) { //TODO use different status view for queue next

    private val trackNumberText: TextView = itemView.findViewById(R.id.track_number)
    private val titleText: TextView = itemView.findViewById(R.id.title)

    override fun bind(item: CollectionItem) {
        super.bind(item)
        val title = item.title

        if (title != null) {
            titleText.text = title
        } else {
            titleText.text = item.name
        }
        val trackNumber = item.trackNumber
        if (trackNumber >= 0) {
            trackNumberText.text = String.format(null as Locale?, "%1$02d.", trackNumber)
        } else {
            trackNumberText.text = ""
        }
    }

}