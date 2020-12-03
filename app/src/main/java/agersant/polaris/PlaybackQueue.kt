package agersant.polaris

import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.DownloadQueue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.math.max


class PlaybackQueue {

    enum class Ordering {
        Sequence, RepeatOne, RepeatAll
    }

    private val _ordering = MutableLiveData(Ordering.Sequence)
    val liveOrdering: LiveData<Ordering> by this::_ordering
    var ordering: Ordering
        get() = _ordering.value!!
        set(value) {
            _ordering.value = value
        }

    private val _items = MutableLiveData(listOf<CollectionItem>())
    val liveItems: LiveData<List<CollectionItem>> get() = _items
    var items: List<CollectionItem>
        get() = _items.value!!
        private set(value) {
            _items.value = value
        }

    fun get(index: Int): CollectionItem? {
        return items.getOrNull(index)
    }

    fun add(item: CollectionItem) {
        items = items.plus(item.clone())
    }

    fun addAll(items: Iterable<CollectionItem>) {
        this.items = this.items.plus(items.map { it.clone() })
    }

    fun replace(items: Iterable<CollectionItem>) {
        this.items = items.map { it.clone() }
    }

    fun remove(item: CollectionItem) {
        items = items.minus(item)
    }

    fun removeAt(index: Int) {
        val newItems = items.toMutableList()
        newItems.removeAt(index)
        items = newItems
    }

    fun clear() {
        items = listOf()
    }

    fun shuffle() {
        items = items.shuffled()
    }

    fun swap(fromPos: Int, toPos: Int) {
        val newItems = items.toMutableList()
        Collections.swap(newItems, fromPos, toPos)

        items = newItems
    }

    // Return negative value if a is going to play before b, positive if a is going to play after b
    fun comparePriorities(currentItem: CollectionItem?, a: CollectionItem, b: CollectionItem): Int {
        val currentIndex = items.indexOfFirst { it === currentItem }
        val playlistSize = items.size
        var scoreA = playlistSize + 1
        var scoreB = playlistSize + 1
        for (i in 0 until playlistSize) {
            val item = items[i]
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


    fun nextTrack(from: CollectionItem?, delta: Int): CollectionItem? {
        if (items.isEmpty()) return null

        if (ordering == Ordering.RepeatOne) return from

        val currentIndex = items.indexOfFirst { it === from }
        if (currentIndex < 0) return items[0]

        val newIndex = currentIndex + delta
        if (newIndex in items.indices) return items[newIndex]

        if (ordering == Ordering.RepeatAll) {
            return if (delta > 0) {
                items[0]
            } else {
                items[items.size - 1]
            }
        }

        return null
    }

    fun hasNextTrack(currentItem: CollectionItem?): Boolean {
        return nextTrack(currentItem, 1) != null
    }

    fun hasPreviousTrack(currentItem: CollectionItem?): Boolean {
        return nextTrack(currentItem, -1) != null
    }

    fun nextItemToDownload(currentItem: CollectionItem?, offlineCache: OfflineCache, downloadQueue: DownloadQueue): CollectionItem? {
        val currentIndex = max(0, items.indexOfFirst { it === currentItem })
        var bestScore = 0
        var bestItem: CollectionItem? = null
        val playlistSize = items.size

        for (i in 0 until playlistSize) {
            val score = (playlistSize + i - currentIndex) % playlistSize
            if (bestItem != null && score > bestScore) {
                continue
            }
            val item = items[i]
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

        val numSongsToPreloadKey = App.resources.getString(R.string.pref_key_num_songs_preload)
        val numSongsToPreloadString = App.preferences.getString(numSongsToPreloadKey, "0")
        val numSongsToPreload = numSongsToPreloadString!!.toInt()
        if (numSongsToPreload in 0 until bestScore) {
            bestItem = null
        }

        return bestItem
    }
}