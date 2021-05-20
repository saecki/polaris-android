package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.api.local.ImageCache.Companion.instance
import agersant.polaris.api.local.LocalAPI
import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.ServerAPI
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.InputStream

class FetchImageTask private constructor(
    private val offlineCache: OfflineCache,
    private val api: API,
    private val serverAPI: ServerAPI,
    private val localAPI: LocalAPI,
    private val item: CollectionItem,
    private val size: ThumbnailSize,
    private val callback: Callback
) : AsyncTask<Unit, Unit, Bitmap>() {

    interface Callback {
        fun onSuccess(bitmap: Bitmap?)
    }

    internal class AsyncDrawable(res: Resources?, val item: CollectionItem) : BitmapDrawable(res, null as Bitmap?)

    companion object {
        @JvmStatic
        fun load(offlineCache: OfflineCache, api: API, serverAPI: ServerAPI, localAPI: LocalAPI, item: CollectionItem, size: ThumbnailSize, callback: Callback) {
            val task = FetchImageTask(offlineCache, api, serverAPI, localAPI, item, size, callback)
            task.executeOnExecutor(THREAD_POOL_EXECUTOR)
        }
    }

    override fun doInBackground(vararg params: Unit): Bitmap? {
        var bitmap: Bitmap? = null
        var fromDiskCache = false

        if (localAPI.hasImage(item, size)) {
            bitmap = localAPI.getImage(item, size)
            fromDiskCache = bitmap != null
        }

        if (bitmap == null) {
            if (!api.isOffline) {
                try {
                    val responseBody = serverAPI.getThumbnail(item.artwork, size)
                    val stream: InputStream = BufferedInputStream(responseBody!!.byteStream())
                    bitmap = BitmapFactory.decodeStream(stream)
                } catch (e: Exception) {
                    println("Error while downloading image: $e")
                }
            }
        }

        if (bitmap != null) {
            val cache = instance
            cache[item.artwork, size] = bitmap
            if (!fromDiskCache) {
                offlineCache.putImage(item, size, bitmap)
            }
        }

        return bitmap
    }

    override fun onPostExecute(bitmap: Bitmap?) {
        if (bitmap != null) {
            callback.onSuccess(bitmap)
        }
    }
}
