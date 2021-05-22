package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.PlaybackQueue
import agersant.polaris.PolarisPlayer
import agersant.polaris.api.API
import agersant.polaris.api.local.OfflineCache
import android.content.Context
import com.google.android.exoplayer2.source.MediaSource
import java.io.File
import java.util.*

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

    private val workers: ArrayList<DownloadQueueWorker> = ArrayList()

    init {
        for (i in 0..1) {
            val file = File(context.externalCacheDir, "stream$i.tmp")
            val worker = DownloadQueueWorker(file, serverAPI, offlineCache, player)
            workers.add(worker)
        }

    }

    @Synchronized
    suspend fun getAudio(item: CollectionItem): MediaSource? {
        val existingWorker = findWorkerWithAudioForItem(item)
        if (existingWorker != null) {
            existingWorker.stopBackgroundDownload()
            return existingWorker.mediaSource
        }

        val newWorker = findIdleWorker() ?: findWorkerToInterrupt()
        if (newWorker == null) {
            println("ERROR: Could not find a worker for download queue.")
            return null
        }

        newWorker.assignItem(item)
        return newWorker.mediaSource
    }

    private fun findWorkerWithAudioForItem(item: CollectionItem): DownloadQueueWorker? {
        for (worker in workers) {
            if (worker.hasMediaSourceFor(item)) {
                return worker
            }
        }
        return null
    }

    fun isStreaming(item: CollectionItem?): Boolean {
        for (worker in workers) {
            if (worker.hasMediaSourceFor(item!!)) {
                return true
            }
        }
        return false
    }

    fun isDownloading(item: CollectionItem?): Boolean {
        for (worker in workers) {
            if (worker.isDownloading(item!!)) {
                return true
            }
        }
        return false
    }

    private fun findIdleWorker(): DownloadQueueWorker? {
        for (worker in workers) {
            if (worker.isIdle) {
                return worker
            }
        }
        return null
    }

    private fun findWorkerToInterrupt(): DownloadQueueWorker? {
        for (worker in workers) {
            if (worker.canBeInterrupted) {
                return worker
            }
        }
        return null
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
