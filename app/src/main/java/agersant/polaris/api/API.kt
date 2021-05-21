package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApplication
import agersant.polaris.R
import agersant.polaris.api.local.ImageCache.Companion.instance
import agersant.polaris.api.local.LocalAPI
import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.ServerAPI
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.WeakReference

class API(context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val offlineModePreferenceKey: String = context.getString(R.string.pref_key_offline)

    private lateinit var offlineCache: OfflineCache
    private lateinit var serverAPI: ServerAPI
    private lateinit var localAPI: LocalAPI

    private val api: IPolarisAPI
        get() = if (isOffline) localAPI else serverAPI


    fun initialize(offlineCache: OfflineCache, serverAPI: ServerAPI, localAPI: LocalAPI) {
        this.offlineCache = offlineCache
        this.serverAPI = serverAPI
        this.localAPI = localAPI
    }

    val isOffline: Boolean
        get() = preferences.getBoolean(offlineModePreferenceKey, false)

    fun loadAudio(item: CollectionItem?, callback: FetchAudioTask.Callback?): FetchAudioTask {
        return FetchAudioTask.load(this, localAPI, serverAPI, item, callback)
    }

    private suspend fun loadThumbnail(item: CollectionItem, size: ThumbnailSize): Bitmap? {
        item.artwork ?: return null

        var bitmap: Bitmap? = null
        var fromDiskCache = false

        if (localAPI.hasImage(item, size)) {
            try {
                bitmap = localAPI.getImage(item, size)
                fromDiskCache = bitmap != null
            } catch (e: IOException) {
                println("Error while loading image from disk: $e")
            }
        }

        if (bitmap == null && !isOffline) {
            try {
                bitmap = serverAPI.getThumbnail(item.artwork, size)
            } catch (e: Exception) {
                println("Error while downloading image: $e")
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

    fun loadThumbnail(
        item: CollectionItem,
        size: ThumbnailSize,
        callback: ImageCallback
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = loadThumbnail(item, size)

            if (bitmap != null) {
                callback.onSuccess(bitmap)
            }
        }
    }

    fun loadThumbnailIntoView(item: CollectionItem, size: ThumbnailSize, view: ImageView) {
        val polarisApplication = PolarisApplication.getInstance()
        val resources = polarisApplication.resources
        val asyncDrawable = AsyncDrawable(resources, item)
        view.setImageDrawable(asyncDrawable)

        val imageViewReference = WeakReference(view)
        loadThumbnail(item, size) { bitmap: Bitmap? ->
            val imageView = imageViewReference.get()
            val drawable = imageView?.drawable
            if (drawable === asyncDrawable && asyncDrawable.item === item) {
                GlobalScope.launch(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    fun browse(path: String, handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = api.browse(path)
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    fun flatten(path: String, handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = api.flatten(path)
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    internal class AsyncDrawable(res: Resources?, val item: CollectionItem) : BitmapDrawable(res, null as Bitmap?) // TODO: replace with coroutines and lifecyle

    fun interface ImageCallback {
        fun onSuccess(bitmap: Bitmap?)
    }
}
