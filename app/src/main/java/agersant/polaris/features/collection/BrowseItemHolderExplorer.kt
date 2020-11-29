package agersant.polaris.features.collection

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.R
import agersant.polaris.api.API
import android.view.View
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView

internal class BrowseItemHolderExplorer(
    api: API,
    playbackQueue: PlaybackQueue,
    adapter: BrowseAdapter,
    itemView: View,
    itemQueueStatusView: View,
    itemQueueNextStatusView: View,
) : BrowseItemHolder(api, playbackQueue, adapter, itemView, itemQueueStatusView, itemQueueNextStatusView) {

    private val text: MaterialTextView = itemView.findViewById(R.id.text)
    private val icon: ImageView = itemView.findViewById(R.id.icon)

    override fun bind(item: CollectionItem) {
        super.bind(item)
        text.text = item.name
        val iconRes = if (item.isDirectory)
            R.drawable.baseline_folder_open_24
        else
            R.drawable.baseline_audiotrack_24

        icon.setImageResource(iconRes)
    }

    init {
        itemView.setOnClickListener(this)
    }
}