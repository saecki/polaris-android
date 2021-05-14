package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.Song
import agersant.polaris.api.API
import agersant.polaris.databinding.ViewBrowseAlbumItemBinding
import agersant.polaris.databinding.ViewQueueStatusBinding
import agersant.polaris.util.formatDuration

internal class BrowseItemHolderAlbumTrack(
    api: API,
    playbackQueue: PlaybackQueue,
    adapter: BrowseAdapter,
    itemBinding: ViewBrowseAlbumItemBinding,
    queueStatusBinding: ViewQueueStatusBinding
) : BrowseItemHolder(api, playbackQueue, adapter, itemBinding.root, queueStatusBinding) {

    private val trackNumberText = itemBinding.trackNumber
    private val titleText = itemBinding.title
    private val artistText = itemBinding.artist
    private val durationText = itemBinding.duration

    override fun bindItem(item: CollectionItem) {
        super.bindItem(item)
        val song = item as Song

        if (song.trackNumber != -1) {
            trackNumberText.text = String.format("%02d", song.trackNumber)
        } else {
            trackNumberText.text = ""
        }
        titleText.text = song.title ?: song.name
        artistText.text = song.artist.orEmpty()
        durationText.text = formatDuration(song.duration)
    }
}
