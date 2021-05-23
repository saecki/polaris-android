package agersant.polaris.api.remote

import agersant.polaris.PlaybackQueue
import agersant.polaris.PolarisPlayer
import agersant.polaris.Song
import agersant.polaris.api.API
import agersant.polaris.api.local.OfflineCache
import android.content.Context
import com.google.android.exoplayer2.source.MediaSource
import java.io.File

class DownloadQueue(
    context: Context,
    private val api: API,
    private val playbackQueue: PlaybackQueue,
    private val player: PolarisPlayer,
    private val offlineCache: OfflineCache,
    serverAPI: ServerAPI
) {

    companion object {
        const val WORKLOAD_CHANGED = "WORKLOAD_CHANGED"
    }

    private val workers = mutableListOf<DownloadQueueWorker>()

    init {
        for (i in 0..1) {
            val file = File(context.externalCacheDir, "stream$i.tmp")
            val worker = DownloadQueueWorker(file, serverAPI, offlineCache, player)
            workers.add(worker)
        }
    }

    @Synchronized
    suspend fun getAudio(song: Song): MediaSource? {
        val existingWorker = findWorkerWithAudioForItem(song)
        if (existingWorker != null) {
            existingWorker.stopBackgroundDownload()
            return existingWorker.streamingMediaSource
        }

        val newWorker = findIdleWorker() ?: findWorkerToInterrupt()
        if (newWorker == null) {
            println("ERROR: Could not find a worker for download queue.")
            return null
        }

        newWorker.assignItem(song)
        return newWorker.streamingMediaSource
    }

    private fun findWorkerWithAudioForItem(song: Song): DownloadQueueWorker? {
        return workers.firstOrNull { it.isStreaming(song) }
    }

    fun isStreaming(song: Song): Boolean {
        return workers.any { it.isStreaming(song) }
    }

    fun isDownloading(item: Song): Boolean {
        return workers.any { it.isDownloading(item) }
    }

    private fun findIdleWorker(): DownloadQueueWorker? {
        return workers.firstOrNull { it.isIdle }
    }

    private fun findWorkerToInterrupt(): DownloadQueueWorker? {
        return workers.firstOrNull { it.canBeInterrupted }
    }

    @Synchronized
    suspend fun downloadNext() {
        if (api.isOffline) {
            return
        }
        val worker = findIdleWorker() ?: return
        val nextItem = playbackQueue.getNextItemToDownload(player.currentItem, offlineCache, this)
        if (nextItem != null) {
            if (!offlineCache.makeSpace(nextItem)) {
                return
            }
            worker.assignItem(nextItem)
            worker.beginBackgroundDownload()
        }
    }
}
