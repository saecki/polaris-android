package agersant.polaris.features.queue

import agersant.polaris.*
import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.DownloadQueue
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class QueueViewModel : ViewModel() {
    var receiver: BroadcastReceiver? = null
    var state: PolarisState = App.state

    val items: LiveData<List<CollectionItem>> = state.playbackQueue.liveItems
    val ordering = state.playbackQueue.liveOrdering
    val itemsState = MutableLiveData(0)

    init {
        subscribeEvents()
    }

    fun clear() {
        state.playbackQueue.clear()
    }

    fun shuffle() {
        state.playbackQueue.shuffle()
    }

    fun setOrdering(ordering: PlaybackQueue.Ordering) {
        state.playbackQueue.ordering = ordering
    }

    private fun subscribeEvents() {
        val filter = IntentFilter()
        filter.addAction(PolarisPlayer.OPENING_TRACK)
        filter.addAction(PolarisPlayer.PLAYING_TRACK)
        filter.addAction(OfflineCache.AUDIO_CACHED)
        filter.addAction(DownloadQueue.WORKLOAD_CHANGED)
        filter.addAction(OfflineCache.AUDIO_REMOVED_FROM_CACHE)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == null) {
                    return
                }
                when (intent.action) {
                    PolarisPlayer.OPENING_TRACK,
                    PolarisPlayer.PLAYING_TRACK,
                    OfflineCache.AUDIO_CACHED,
                    OfflineCache.AUDIO_REMOVED_FROM_CACHE,
                    DownloadQueue.WORKLOAD_CHANGED,
                    -> itemsState.value = Random.nextInt()
                }
            }
        }
        App.instance.registerReceiver(receiver, filter)
    }
}