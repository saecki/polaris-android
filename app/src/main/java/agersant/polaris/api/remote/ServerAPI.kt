package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.R
import agersant.polaris.api.ItemsCallback
import agersant.polaris.api.ThumbnailSize
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.source.MediaSource
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import java.io.IOException

class ServerAPI(context: Context) : IRemoteAPI {

    companion object {
        private lateinit var serverAddressKey: String
        private lateinit var preferences: SharedPreferences

        @JvmStatic
        val aPIRootURL: String
            get() {
                var address = preferences.getString(serverAddressKey, "")!!.trim()
                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                    address = "http://$address"
                }
                address = address.replace("/$".toRegex(), "")
                return "$address/api"
            }
    }

    private val client: HttpClient
    private var downloadQueue: DownloadQueue? = null
    private var currentVersion: IRemoteAPI? = null
    val auth: Auth

    init {
        serverAddressKey = context.getString(R.string.pref_key_server_url)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        auth = Auth(context)
        client = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            engine {
                config {
                    retryOnConnectionFailure(true)
                }
            }
        }

        preferences.registerOnSharedPreferenceChangeListener { _: SharedPreferences?, _: String? ->
            currentVersion = null
        }
    }

    fun initialize(downloadQueue: DownloadQueue?) {
        this.downloadQueue = downloadQueue
    }

    private suspend fun fetchAPIVersion() {
        if (currentVersion != null) return

        try {
            val version = client.get<APIVersion>("$aPIRootURL/version")
            currentVersion = selectImplementation(version)
        } catch (e: Exception) {
            println("Error fetching API version $e")
        }
    }

    private fun fetchAPIVersionAsync(callback: VersionCallback) {
        MainScope().launch(Dispatchers.IO) {
            fetchAPIVersion()

            if (currentVersion != null) {
                callback.onSuccess()
            } else {
                callback.onError()
            }
        }
    }

    private fun selectImplementation(version: APIVersion): IRemoteAPI {
        val requestQueue = RequestQueue(auth)
        return when {
            version.major < 3 -> APIVersion2(downloadQueue, requestQueue)
            version.major < 4 -> APIVersion3(downloadQueue, requestQueue)
            version.major < 5 -> APIVersion4(downloadQueue, requestQueue)
            version.major < 6 -> APIVersion5(downloadQueue, requestQueue)
            else -> APIVersion6(downloadQueue!!, requestQueue)
        }
    }

    override fun getRandomAlbums(handlers: ItemsCallback) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.getRandomAlbums(handlers)
            }

            override fun onError() {
                handlers.onError()
            }
        })
    }

    override fun getRecentAlbums(handlers: ItemsCallback) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.getRecentAlbums(handlers)
            }

            override fun onError() {
                handlers.onError()
            }
        })
    }

    override fun setLastFMNowPlaying(path: String) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.setLastFMNowPlaying(path)
            }

            override fun onError() {}
        })
    }

    override fun scrobbleOnLastFM(path: String) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.scrobbleOnLastFM(path)
            }

            override fun onError() {}
        })
    }

    @Throws(IOException::class)
    override fun getAudio(item: CollectionItem): MediaSource? {
        suspend { fetchAPIVersion() }
        return currentVersion?.getAudio(item)
    }

    @Throws(IOException::class)
    override fun getAudio(path: String): ResponseBody? {
        suspend { fetchAPIVersion() }
        return currentVersion?.getAudio(path)
    }

    @Throws(IOException::class)
    override fun getThumbnail(path: String, size: ThumbnailSize): ResponseBody? {
        suspend { fetchAPIVersion() }
        return currentVersion?.getThumbnail(path, size)
    }

    override fun getAudioUri(path: String): Uri? {
        suspend { fetchAPIVersion() }
        return currentVersion?.getAudioUri(path)
    }

    override fun getThumbnailUri(path: String, size: ThumbnailSize): Uri? {
        suspend { fetchAPIVersion() }
        return currentVersion?.getThumbnailUri(path, size)
    }

    override fun browse(path: String, handlers: ItemsCallback) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.browse(path, handlers)
            }

            override fun onError() {
                handlers.onError()
            }
        })
    }

    override fun flatten(path: String, handlers: ItemsCallback) {
        fetchAPIVersionAsync(object : VersionCallback {
            override fun onSuccess() {
                currentVersion!!.flatten(path, handlers)
            }

            override fun onError() {
                handlers.onError()
            }
        })
    }

    @Serializable
    private class APIVersion(
        val major: Int,
        val minor: Int,
    )

    private interface VersionCallback {
        fun onSuccess()
        fun onError()
    }

}
