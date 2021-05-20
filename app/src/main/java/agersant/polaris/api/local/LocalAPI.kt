package agersant.polaris.api.local

import agersant.polaris.CollectionItem
import agersant.polaris.api.IPolarisAPI
import agersant.polaris.api.ItemsCallback
import agersant.polaris.api.ThumbnailSize
import android.graphics.Bitmap
import com.google.android.exoplayer2.source.MediaSource
import java.io.IOException
import kotlin.jvm.Throws

class LocalAPI : IPolarisAPI {
    private lateinit var offlineCache: OfflineCache

    fun initialize(offlineCache: OfflineCache) {
        this.offlineCache = offlineCache
    }

    fun hasAudio(item: CollectionItem): Boolean {
        val path = item.path
        return offlineCache.hasAudio(path)
    }

    @Throws(IOException::class)
    override fun getAudio(item: CollectionItem): MediaSource {
        val path = item.path
        return offlineCache.getAudio(path)
    }

    fun hasImage(item: CollectionItem, size: ThumbnailSize): Boolean {
        val path = item.artwork
        return offlineCache.hasImage(path, size)
    }

    @Throws(IOException::class)
    fun getImage(item: CollectionItem, size: ThumbnailSize): Bitmap? {
        return offlineCache.getImage(item.artwork, size)
    }

    override fun browse(path: String, handlers: ItemsCallback) {
        val items = offlineCache.browse(path)
        if (items != null) {
            handlers.onSuccess(items)
        } else {
            handlers.onError()
        }
    }

    override fun flatten(path: String, handlers: ItemsCallback) {
        val items = offlineCache.flatten(path)
        if (items != null) {
            handlers.onSuccess(items)
        } else {
            handlers.onError()
        }
    }
}
