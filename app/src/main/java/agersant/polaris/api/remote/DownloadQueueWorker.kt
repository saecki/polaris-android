package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApp
import agersant.polaris.PolarisPlayer
import agersant.polaris.api.local.OfflineCache
import android.content.Intent
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class DownloadQueueWorker(
    private val scratchFile: File,
    private val serverAPI: ServerAPI,
    private val offlineCache: OfflineCache,
    private val player: PolarisPlayer
) {

    private sealed class State {
        object Uninitialized : State() // TODO better name
        class Initialized(val item: CollectionItem, val mediaSource: MediaSource, val dataSource: DataSource) : State()
        class Downloading(val item: CollectionItem, val mediaSource: MediaSource, val dataSource: DataSource, val job: Job) : State()
    }

    companion object {
        private const val BUFFER_SIZE = 1024 * 64 // 64 kB
    }

    private var state: State = State.Uninitialized

    val mediaSource: MediaSource?
        get() = state.let {
            return when (it) {
                is State.Initialized -> it.mediaSource
                is State.Downloading -> null
                is State.Uninitialized -> null
            }
        }

    private val isUsedByPlayer: Boolean
        get() = state.let {
            it is State.Initialized && player.isUsing(it.mediaSource)
        }

    val isIdle: Boolean
        get() = state.let {
            if (it is State.Downloading && it.job.isActive) {
                return false
            }
            return !isUsedByPlayer
        }

    val canBeInterrupted: Boolean
        get() = state.let {
            return !isUsedByPlayer
        }

    fun hasMediaSourceFor(item: CollectionItem): Boolean = state.let {
        return when (it) {
            is State.Uninitialized -> false
            is State.Initialized -> it.item.path == item.path
            is State.Downloading -> it.item.path == item.path
        }
    }

    fun isDownloading(item: CollectionItem): Boolean = state.let {
        when (it) {
            is State.Downloading -> it.item.path == item.path && it.job.isActive
            else -> false
        }
    }

    suspend fun assignItem(item: CollectionItem): Boolean {
        reset()
        val uri = withContext(Dispatchers.IO) { serverAPI.getAudioUri(item.path) }
        uri ?: return false

        val dsf = PolarisExoPlayerDataSourceFactory(offlineCache, serverAPI.auth, scratchFile, item)
        val mediaSource = ProgressiveMediaSource.Factory(dsf).createMediaSource(MediaItem.fromUri(uri))
        val dataSource = dsf.createDataSource()
        state = State.Initialized(item, mediaSource, dataSource)
        broadcast(DownloadQueue.WORKLOAD_CHANGED)
        return true
    }

    suspend fun beginBackgroundDownload(): Boolean = state.run {
        if (this !is State.Initialized) return false

        val uri = withContext(Dispatchers.IO) { serverAPI.getAudioUri(item.path) }
        uri ?: return false

        println("Beginning background download for: " + item.path)
        val job = PolarisApp.instance.scope.launch {
            download(dataSource, uri)
        }

        state = State.Downloading(item, mediaSource, dataSource, job)
        broadcast(DownloadQueue.WORKLOAD_CHANGED)

        return true
    }

    fun stopBackgroundDownload() = state.run {
        if (this is State.Downloading) {
            job.cancel()
            state = State.Initialized(item, mediaSource, dataSource)
            broadcast(DownloadQueue.WORKLOAD_CHANGED)
        }
    }

    private fun reset() = state.run {
        if (this is State.Downloading) {
            job.cancel()
        }

        state = State.Uninitialized
    }

    private fun broadcast(event: String) {
        val application = PolarisApp.instance
        val intent = Intent().setAction(event)
        application.sendBroadcast(intent)
    }

    private suspend fun download(dataSource: DataSource, uri: Uri) = withContext(Dispatchers.IO) {
        val dataSpec = DataSpec(uri)
        val buffer = ByteArray(BUFFER_SIZE)

        try {
            dataSource.open(dataSpec)
            while (true) {
                val bytesRead = dataSource.read(buffer, 0, BUFFER_SIZE)
                if (bytesRead == 0 || bytesRead == C.RESULT_END_OF_INPUT) {
                    break
                }
            }
        } catch (e: Exception) {
            println("Download task error during reads: " + e + " (" + dataSpec.uri + ")")
        }

        try {
            dataSource.close()
        } catch (e: Exception) {
            println("Download task error during close: $e")
        }
    }

}
