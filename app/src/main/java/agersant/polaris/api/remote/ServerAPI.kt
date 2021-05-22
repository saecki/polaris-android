package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.IO
import agersant.polaris.R
import agersant.polaris.api.ItemsCallback
import agersant.polaris.api.ThumbnailSize
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.source.MediaSource
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

class ServerAPI(context: Context) : IRemoteAPI {

    companion object {
        private lateinit var serverAddressKey: String
        private lateinit var preferences: SharedPreferences

        @JvmStatic
        val apiRootURL: String
            get() {
                var address = preferences.getString(serverAddressKey, "")!!.trim()
                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                    address = "http://$address"
                }
                address = address.replace("/$".toRegex(), "")
                return "$address/api"
            }
    }

    private lateinit var downloadQueue: DownloadQueue
    private val client = buildClient()
    val auth = Auth(context)

    init {
        serverAddressKey = context.getString(R.string.pref_key_server_url)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.registerOnSharedPreferenceChangeListener { _: SharedPreferences?, _: String? ->
            currentVersion = null
        }
    }

    fun initialize(downloadQueue: DownloadQueue) {
        this.downloadQueue = downloadQueue
    }

    private fun buildClient(config: HttpClientConfig<OkHttpConfig>.() -> Unit = {}): HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(IO.json)
        }
        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }
        config()
    }

    private var currentVersion: IRemoteAPI? = null
    private suspend fun fetchAPIVersion(): IRemoteAPI? {
        if (currentVersion != null) return currentVersion

        return try {
            val version = client.get<APIVersion>("$apiRootURL/version")
            currentVersion = selectImplementation(version)
            currentVersion
        } catch (e: Exception) {
            println("Error fetching API version $e")
            null
        }
    }

    private fun selectImplementation(version: APIVersion): IRemoteAPI {
        val client = buildClient {
            engine {
                addInterceptor(auth)
            }
        }
        return when {
            version.major < 3 -> APIVersion2(downloadQueue, client)
            version.major < 4 -> APIVersion3(downloadQueue, client)
            version.major < 5 -> APIVersion4(downloadQueue, client)
            version.major < 6 -> APIVersion5(downloadQueue, client)
            else -> APIVersion6(downloadQueue, client)
        }
    }

    override suspend fun browse(path: String): List<CollectionItem>? {
        return fetchAPIVersion()?.browse(path)
    }

    override suspend fun flatten(path: String): List<CollectionItem>? {
        return fetchAPIVersion()?.flatten(path)
    }

    override suspend fun getRandomAlbums(): List<CollectionItem>? {
        return fetchAPIVersion()?.getRandomAlbums()
    }

    fun getRandomAlbums(handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = getRandomAlbums()
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    override suspend fun getRecentAlbums(): List<CollectionItem>? {
        return fetchAPIVersion()?.getRandomAlbums()
    }

    fun getRecentAlbums(handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(Dispatchers.IO) {
            val items = getRecentAlbums()
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    override suspend fun setLastFmNowPlaying(path: String): Boolean {
        return fetchAPIVersion()?.setLastFmNowPlaying(path) ?: false
    }

    fun setLastFmNowPlayingAsync(path: String) { // TODO: remove when possible
        GlobalScope.launch { setLastFmNowPlaying(path) }
    }

    override suspend fun scrobbleOnLastFm(path: String): Boolean {
        return fetchAPIVersion()?.scrobbleOnLastFm(path) ?: false
    }

    fun scrobbleOnLastFmAsync(path: String) { // TODO: remove when possible
        GlobalScope.launch { scrobbleOnLastFm(path) }
    }

    override suspend fun getAudio(item: CollectionItem): MediaSource? {
        return fetchAPIVersion()?.getAudio(item)
    }

    override suspend fun getThumbnail(path: String, size: ThumbnailSize): Bitmap? {
        return fetchAPIVersion()?.getThumbnail(path, size)
    }

    override suspend fun getAudioUri(path: String): Uri? {
        return fetchAPIVersion()?.getAudioUri(path)
    }

    fun getAudioUriSync(path: String): Uri? { // TODO: remove when possible
        return runBlocking { getAudioUri(path) }
    }

    @Serializable
    private class APIVersion(
        val major: Int,
        val minor: Int,
    )

}
