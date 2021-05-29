package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.Directory
import agersant.polaris.R
import agersant.polaris.Serializers
import agersant.polaris.Song
import agersant.polaris.api.ItemsCallback
import agersant.polaris.api.ThumbnailSize
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.source.MediaSource
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class ServerAPI(private val context: Context) : IRemoteAPI {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val authTokenKey = context.getString(R.string.pref_key_auth_token)
    private val serverUrlKey = context.getString(R.string.pref_key_server_url)
    private val usernameKey = context.getString(R.string.pref_key_username)
    private val passwordKey = context.getString(R.string.pref_key_password)
    private lateinit var downloadQueue: DownloadQueue
    private val client = buildClient()
    val cookieAuth = CookieAuth(context)
    private var currentVersion: IRemoteAPI? = null

    init {
        preferences.registerOnSharedPreferenceChangeListener { _, key ->
            when (key) {
                serverUrlKey, usernameKey, passwordKey -> {
                    currentVersion = null
                }
                else -> Unit
            }
        }
    }

    private val apiRootUrl: String
        get() {
            val address = preferences.getString(serverUrlKey, "")!!.trim().trimEnd('/')

            return if (!address.startsWith("http://") && !address.startsWith("https://")) {
                "http://$address/api"
            } else {
                "$address/api"
            }
        }

    fun initialize(downloadQueue: DownloadQueue) {
        this.downloadQueue = downloadQueue
    }

    private fun buildClient(configure: HttpClientConfig<OkHttpConfig>.() -> Unit = {}) = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Serializers.json)
        }
        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }
        configure()
    }

    private suspend fun fetchAPIVersion(): IRemoteAPI? {
        if (currentVersion != null) return currentVersion

        return try {
            val version = client.get<APIVersion>("$apiRootUrl/version")
            currentVersion = selectImplementation(version)
            currentVersion
        } catch (e: Exception) {
            println("Error fetching API version $e")
            null
        }
    }

    private fun selectImplementation(version: APIVersion): IRemoteAPI {
        val authClient = buildClient {
            when {
                version.major <= 6 -> configureBasicCookieAuth()
                else -> configureBearerAuth()
            }
        }

        return when {
            version.major <= 2 -> APIVersion2(downloadQueue, authClient, apiRootUrl)
            version.major <= 3 -> APIVersion3(downloadQueue, authClient, apiRootUrl)
            version.major <= 4 -> APIVersion4(downloadQueue, authClient, apiRootUrl)
            version.major <= 5 -> APIVersion5(downloadQueue, authClient, apiRootUrl)
            version.major <= 6 -> APIVersion6(downloadQueue, authClient, apiRootUrl)
            else -> APIVersion7(downloadQueue, authClient, apiRootUrl)
        }
    }

    private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.configureBasicCookieAuth() {
        println("Using basic authentication")

        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(Auth) {
            basic {
                sendWithoutRequest { false }
                credentials {
                    val username = preferences.getString(usernameKey, "")!!
                    val password = preferences.getString(passwordKey, "")!!
                    BasicAuthCredentials(username, password)
                }
            }
        }
    }

    private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.configureBearerAuth() {
        println("Using bearer authentication")

        install(Auth) {
            bearer {
                loadTokens {
                    val authToken = preferences.getString(authTokenKey, null)
                    if (authToken != null) {
                        return@loadTokens BearerTokens(accessToken = authToken, refreshToken = "")
                    }

                    val username = preferences.getString(usernameKey, "")!!
                    val password = preferences.getString(passwordKey, "")!!
                    val auth = client.post<Authorization>("$apiRootUrl/auth/") {
                        contentType(ContentType.Application.Json)
                        body = Credentials(username, password)
                    }

                    preferences.edit().putString(authTokenKey, auth.token).apply()
                    BearerTokens(accessToken = auth.token, refreshToken = "")
                }
            }
        }
    }

    override suspend fun browse(path: String): List<CollectionItem>? = withContext(IO) {
        fetchAPIVersion()?.browse(path)
    }

    override suspend fun flatten(path: String): List<Song>? = withContext(IO) {
        fetchAPIVersion()?.flatten(path)
    }

    override suspend fun getRandomAlbums(): List<Directory>? = withContext(IO) {
        fetchAPIVersion()?.getRandomAlbums()
    }

    fun getRandomAlbums(handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(IO) {
            val items = getRandomAlbums()
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    override suspend fun getRecentAlbums(): List<Directory>? = withContext(IO) {
        fetchAPIVersion()?.getRecentAlbums()
    }

    fun getRecentAlbums(handlers: ItemsCallback) { // TODO: remove when possible
        GlobalScope.launch(IO) {
            val items = getRecentAlbums()
            if (items != null) {
                handlers.onSuccess(items)
            } else {
                handlers.onError()
            }
        }
    }

    override suspend fun setLastFmNowPlaying(path: String): Boolean = withContext(IO) {
        fetchAPIVersion()?.setLastFmNowPlaying(path) ?: false
    }

    override suspend fun scrobbleOnLastFm(path: String): Boolean = withContext(IO) {
        fetchAPIVersion()?.scrobbleOnLastFm(path) ?: false
    }

    override suspend fun getAudio(item: Song): MediaSource? = withContext(IO) {
        fetchAPIVersion()?.getAudio(item)
    }

    override suspend fun getThumbnail(path: String, size: ThumbnailSize): Bitmap? = withContext(IO) {
        fetchAPIVersion()?.getThumbnail(path, size)
    }

    override suspend fun getAudioUri(path: String): Uri? = withContext(IO) {
        fetchAPIVersion()?.getAudioUri(path)
    }

    @Serializable
    private class APIVersion(
        val major: Int,
        val minor: Int,
    )

}
