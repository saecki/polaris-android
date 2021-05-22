package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApp
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
import com.google.android.exoplayer2.source.MediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    suspend fun loadAudio(item: CollectionItem): MediaSource? {
        if (localAPI.hasAudio(item)) {
            val source = localAPI.getAudio(item)
            return source ?: run {
                println("IO error while reading offline cache for ${item.path}")
                null
            }
        } else if (!isOffline) {
            val source = serverAPI.getAudio(item)
            return source ?: run {
                println("IO error while querying server API for ${item.path}")
                null
            }
        }

        return null
    }

    private suspend fun loadThumbnail(item: CollectionItem, size: ThumbnailSize): Bitmap? {
        item.artwork ?: return null

        var bitmap: Bitmap? = null
        var fromDiskCache = false

        if (localAPI.hasImage(item, size)) {
            bitmap = localAPI.getImage(item, size)
            fromDiskCache = bitmap != null
        }

        if (bitmap == null && !isOffline) {
            bitmap = serverAPI.getThumbnail(item.artwork, size)
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

    fun loadThumbnail( // TODO: remove when possible
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

    fun loadThumbnailIntoView(item: CollectionItem, size: ThumbnailSize, view: ImageView) { // TODO: remove when possible
        val polarisApplication = PolarisApp.instance
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

    suspend fun browse(path: String): List<CollectionItem>? {
        return api.browse(path)
    }

    fun browse(path: String, handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = browse(path)
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    suspend fun flatten(path: String): List<CollectionItem>? {
        return api.flatten(path)
    }

    fun flatten(path: String, handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = flatten(path)
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
