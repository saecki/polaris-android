package agersant.polaris

import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.DownloadQueue
import android.content.Intent
import androidx.preference.PreferenceManager
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.max
import kotlin.math.min

class PlaybackQueue internal constructor() {

    @Serializable
    enum class Ordering {
        SEQUENCE,
        REPEAT_ONE,
        REPEAT_ALL,
    }

    @Serializable
    internal data class State(
        val queueOrdering: Ordering = Ordering.SEQUENCE,
        val queueContent: MutableList<Song> = mutableListOf(),
        val queueIndex: Int = -1,
        val trackProgress: Float = 0f,
    ) {
        companion object {
            const val VERSION = 5
        }
    }

    companion object {
        const val CHANGED_ORDERING = "CHANGED_ORDERING"
        const val QUEUED_ITEM = "QUEUED_ITEM"
        const val QUEUED_ITEMS = "QUEUED_ITEMS"
        const val OVERWROTE_QUEUE = "OVERWROTE_QUEUE"
        const val NO_LONGER_EMPTY = "NO_LONGER_EMPTY"
        const val REMOVED_ITEM = "REMOVED_ITEM"
        const val REMOVED_ITEMS = "REMOVED_ITEMS"
        const val REORDERED_ITEMS = "REORDERED_ITEMS"
    }

    var ordering: Ordering = Ordering.SEQUENCE
        set(value) {
            field = value
            broadcast(CHANGED_ORDERING)
        }

    private var clearedContent: MutableList<Song>? = null
    var content: MutableList<Song> = mutableListOf()
        set(value) {
            field = value
            broadcast(OVERWROTE_QUEUE)
        }

    val size: Int
        @JvmName("size")
        get() = content.size

    val isEmpty: Boolean
        get() = content.isEmpty()

    // Return negative value if a is going to play before b, positive if a is going to play after b
    fun comparePriorities(currentItem: Song?, a: Song, b: Song): Int {
        val currentIndex = content.indexOf(currentItem)
        val playlistSize = content.size

        var scoreA = playlistSize + 1
        var scoreB = playlistSize + 1
        for (i in 0 until playlistSize) {
            val item = content[i]
            val path = item.path
            val score = (playlistSize + i - currentIndex) % playlistSize

            if (score < scoreA && path == a.path) {
                scoreA = score
            }
            if (score < scoreB && path == b.path) {
                scoreB = score
            }
        }

        return scoreA - scoreB
    }

    private fun addItemInternal(item: Song) {
        content.removeIf { it.path == item.path }
        content.add(item)
    }

    fun addItems(items: List<Song>) {
        val wasEmpty = size == 0
        for (item in items) {
            addItemInternal(item)
        }
        broadcast(QUEUED_ITEMS)
        if (wasEmpty) {
            broadcast(NO_LONGER_EMPTY)
        }
    }

    fun addItem(item: Song) {
        val wasEmpty = size == 0
        addItemInternal(item)
        broadcast(QUEUED_ITEM)
        if (wasEmpty) {
            broadcast(NO_LONGER_EMPTY)
        }
    }

    fun remove(position: Int) {
        content.removeAt(position)
        broadcast(REMOVED_ITEM)
    }

    fun clear() {
        clearedContent = content
        content = mutableListOf()
        broadcast(REMOVED_ITEMS)
    }

    fun restore() {
        clearedContent?.let {
            if (it.isEmpty()) return

            content = it
            clearedContent = null
            broadcast(QUEUED_ITEMS)
        }
    }

    fun swap(fromPosition: Int, toPosition: Int) {
        Collections.swap(content, fromPosition, toPosition)
        broadcast(REORDERED_ITEMS)
    }

    fun move(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }
        val low = min(fromPosition, toPosition)
        val high = max(fromPosition, toPosition)
        val distance = if (fromPosition < toPosition) -1 else 1
        Collections.rotate(content.subList(low, high + 1), distance)
        broadcast(REORDERED_ITEMS)
    }

    fun getItem(position: Int): Song? {
        return content.getOrNull(position)
    }

    fun getNextTrack(from: Song?, delta: Int): Song? {
        if (content.isEmpty()) {
            return null
        }
        return if (ordering == Ordering.REPEAT_ONE) {
            from
        } else {
            val currentIndex = content.indexOf(from)
            if (currentIndex < 0) {
                content[0]
            } else {
                val newIndex = currentIndex + delta
                if (newIndex >= 0 && newIndex < content.size) {
                    content[newIndex]
                } else if (ordering == Ordering.REPEAT_ALL) {
                    if (delta > 0) {
                        content[0]
                    } else {
                        content[content.size - 1]
                    }
                } else {
                    null
                }
            }
        }
    }

    fun hasNextTrack(currentItem: Song?): Boolean {
        return getNextTrack(currentItem, 1) != null
    }

    fun hasPreviousTrack(currentItem: Song?): Boolean {
        return getNextTrack(currentItem, -1) != null
    }

    private fun broadcast(event: String) {
        val application = PolarisApp.instance
        val intent = Intent().setAction(event)
        application.sendBroadcast(intent)
    }

    fun getNextItemToDownload(
        currentItem: Song?,
        offlineCache: OfflineCache,
        downloadQueue: DownloadQueue
    ): Song? {
        val currentIndex = max(0, content.indexOf(currentItem))
        var bestScore = 0
        var bestItem: Song? = null
        val playlistSize = content.size
        for (i in 0 until playlistSize) {
            val score = (playlistSize + i - currentIndex) % playlistSize
            if (bestItem != null && score > bestScore) {
                continue
            }
            val item = content[i]
            if (item === currentItem) {
                continue
            }
            if (offlineCache.hasAudio(item.path)) {
                continue
            }
            if (downloadQueue.isDownloading(item)) {
                continue
            }
            bestScore = score
            bestItem = item
        }
        val application = PolarisApp.instance
        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        val resources = application.resources
        val numSongsToPreloadKey = resources.getString(R.string.pref_key_num_songs_preload)
        val numSongsToPreload = preferences.getString(numSongsToPreloadKey, "0")!!.toInt()
        if (numSongsToPreload in 0 until bestScore) {
            bestItem = null
        }
        return bestItem
    }
}
