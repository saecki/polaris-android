package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BrowseAdapterAlbum(private val api: API, private val playbackQueue: PlaybackQueue, recyclerView: RecyclerView) : BrowseAdapter() {

    private var discSizes: SparseIntArray? = null
    private var numDiscHeaders = 0

    override var items: List<CollectionItem>
        get() = super.items
        set(value) {
            discSizes = SparseIntArray()
            for (item in value) {
                val discNumber = item.discNumber
                discSizes!!.put(discNumber, 1 + discSizes!![discNumber, 0])
            }
            numDiscHeaders = discSizes!!.size()
            if (numDiscHeaders == 1) {
                numDiscHeaders = 0
            }
            super.items = value
        }

    override fun getItemCount(): Int {
        return super.getItemCount() + numDiscHeaders
    }

    override fun getItemViewType(position: Int): Int {
        var index = 0
        for (discIndex in 0 until numDiscHeaders) {
            if (position == index) {
                return AlbumViewType.DISC_HEADER.ordinal
            }
            index += discSizes!!.valueAt(discIndex) + 1
            if (position < index) {
                break
            }
        }
        return AlbumViewType.TRACK.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseItemHolder {
        val itemQueueStatusView = LayoutInflater.from(parent.context).inflate(R.layout.view_browse_item_queued, parent, false)
        return if (viewType == AlbumViewType.DISC_HEADER.ordinal) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_browse_album_disc, parent, false)
            BrowseItemHolderAlbumDiscHeader(api, playbackQueue, this, itemView, itemQueueStatusView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_browse_album_item, parent, false)
            BrowseItemHolderAlbumTrack(api, playbackQueue, this, itemView, itemQueueStatusView)
        }
    }

    override fun onBindViewHolder(holder: BrowseItemHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        var position = position
        if (holder is BrowseItemHolderAlbumTrack) {

            // Assign track item
            if (numDiscHeaders > 0) {
                var index = 0
                for (discIndex in 0 until discSizes!!.size()) {
                    if (position <= index) {
                        break
                    }
                    position--
                    index += discSizes!!.valueAt(discIndex)
                }
            }
            holder.bind(items[position])
        } else {

            // Assign disc number
            val header = holder as BrowseItemHolderAlbumDiscHeader
            var index = 0
            for (discIndex in 0 until discSizes!!.size()) {
                if (position == index) {
                    header.setDiscNumber(discSizes!!.keyAt(discIndex))
                }
                index += discSizes!!.valueAt(discIndex) + 1
            }
        }
    }

    internal enum class AlbumViewType {
        DISC_HEADER, TRACK
    }
}