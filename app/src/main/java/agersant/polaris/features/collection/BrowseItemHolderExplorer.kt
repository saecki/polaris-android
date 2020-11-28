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
) : BrowseItemHolder(api, playbackQueue, adapter, itemView, itemQueueStatusView) {

    private val text: MaterialTextView = itemView.findViewById(R.id.text)
    private val icon: ImageView = itemView.findViewById(R.id.icon)

    override fun bind(item: CollectionItem) {
        super.bind(item)
        text.text = item.name
        val iconRes = if (item.isDirectory)
            R.drawable.ic_folder_open_black_24dp
        else
            R.drawable.ic_audiotrack_black_24dp

        icon.setImageResource(iconRes)
    }

    init {
        text.setOnClickListener(this)
    }
}