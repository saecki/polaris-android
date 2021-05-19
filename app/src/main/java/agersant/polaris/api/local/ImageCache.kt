package agersant.polaris.api.local

import agersant.polaris.api.ThumbnailSize
import android.graphics.Bitmap
import androidx.collection.LruCache

internal class ImageCache private constructor() {

    private data class ImageKey(val path: String, val size: ThumbnailSize)

    companion object {
        @JvmStatic
        val instance by lazy { ImageCache() }
    }

    private val lruCache: LruCache<ImageKey, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        lruCache = object : LruCache<ImageKey, Bitmap>(cacheSize) {
            override fun sizeOf(key: ImageKey, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    operator fun get(path: String, size: ThumbnailSize): Bitmap? {
        return lruCache[ImageKey(path, size)]
    }

    operator fun set(path: String, size: ThumbnailSize, value: Bitmap) {
        lruCache.put(ImageKey(path, size), value)
    }
}
