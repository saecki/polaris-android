package agersant.polaris.features.collection

import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BrowseAdapterExplorer(private val api: API, private val playbackQueue: PlaybackQueue) : BrowseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseItemHolder {
        val itemQueueStatusView = LayoutInflater.from(parent.context).inflate(R.layout.view_browse_item_queued, parent, false)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_browse_explorer_item, parent, false)
        return BrowseItemHolderExplorer(api, playbackQueue, this, itemView, itemQueueStatusView)
    }
}