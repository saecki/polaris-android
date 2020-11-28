package agersant.polaris.features.queue

import agersant.polaris.App
import agersant.polaris.CollectionItem
import agersant.polaris.PolarisState
import agersant.polaris.R
import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.DownloadQueue
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class QueueItemHolder(
    private val appState: PolarisState,
    private val queueItemView: QueueItemView,
) : RecyclerView.ViewHolder(queueItemView), View.OnClickListener {

    private val artwork: ImageView = queueItemView.findViewById(R.id.artwork)
    private val titleText: TextView = queueItemView.findViewById(R.id.title)
    private val artistText: TextView = queueItemView.findViewById(R.id.artist)
    private val statusIcon: ImageView = queueItemView.findViewById(R.id.status_icon)
    private var item: CollectionItem? = null
    private var itemState: QueueItemState? = null
    private var updateIconTask: IconUpdateTask? = null

    init {
        queueItemView.setOnClickListener(this)
    }

    private class IconUpdateTask(
        itemHolder: QueueItemHolder,
        private val item: CollectionItem?,
        private val offlineCache: OfflineCache,
        private val downloadQueue: DownloadQueue,
    ) : AsyncTask<Void?, Void?, QueueItemState>() {

        private val itemHolderWeakReference: WeakReference<QueueItemHolder> = WeakReference(itemHolder)

        override fun doInBackground(vararg params: Void?): QueueItemState {
            return when {
                offlineCache.hasAudio(item!!.path) -> QueueItemState.Downloaded
                downloadQueue.isDownloading(item) -> QueueItemState.Downloading
                downloadQueue.isStreaming(item) -> QueueItemState.Streaming
                else -> QueueItemState.Idle
            }
        }

        override fun onPostExecute(state: QueueItemState) {
            val itemHolder = itemHolderWeakReference.get() ?: return
            if (itemHolder.item !== item) {
                return
            }
            itemHolder.setState(state)
        }
    }

    private fun beginIconUpdate() {
        updateIconTask = IconUpdateTask(this, item, appState.offlineCache, appState.downloadQueue)
        updateIconTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun bind(item: CollectionItem) {
        val isNewItem = item !== this.item
        this.item = item
        if (isNewItem) {
            titleText.text = item.title
            artistText.text = item.artist
            setState(QueueItemState.Idle)
        }
        val isPlaying = appState.player.currentItem === item
        queueItemView.setIsPlaying(isPlaying)
        if (updateIconTask != null) {
            updateIconTask?.cancel(true)
            updateIconTask = null
        }
        beginIconUpdate()
        if (item.artwork != null) {
            appState.api.loadImageIntoView(item, artwork)
        }
    }

    private fun setState(newState: QueueItemState) {
        itemState = newState
        when (itemState) {
            QueueItemState.Idle -> {
                statusIcon.setImageDrawable(null)
                statusIcon.contentDescription = ""
            }
            QueueItemState.Streaming -> {
                statusIcon.setImageResource(R.drawable.baseline_play_arrow_24)
                statusIcon.contentDescription = App.resources.getString(R.string.queue_streaming)
            }
            QueueItemState.Downloading -> {
                statusIcon.setImageResource(R.drawable.baseline_sync_24)
                statusIcon.contentDescription = App.resources.getString(R.string.queue_downloading)
            }
            QueueItemState.Downloaded -> {
                statusIcon.setImageResource(R.drawable.baseline_check_circle_24)
                statusIcon.contentDescription = App.resources.getString(R.string.queue_cached)
            }
        }
    }

    override fun onClick(view: View) {
        appState.player.play(item)
    }
}
