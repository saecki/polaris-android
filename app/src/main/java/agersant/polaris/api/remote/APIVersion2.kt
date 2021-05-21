package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.api.ThumbnailSize
import agersant.polaris.api.remote.ServerAPI.Companion.apiRootURL
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class APIVersion2(
    downloadQueue: DownloadQueue,
    client: HttpClient,
) : APIBase(downloadQueue, client) {

    override fun getAudioUrl(path: String): String {
        return "$apiRootURL/serve/$path"
    }

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        val serverAddress = apiRootURL
        return "$serverAddress/serve/$path"
    }

    override suspend fun browse(path: String): List<CollectionItem>? {
        val url = "$apiRootURL/browse/$path"
        return try {
            client.get(url)
        } catch (e: Exception) {
            println("Error browsing url: $url: $e")
            null
        }
    }

    override suspend fun flatten(path: String): List<CollectionItem>? {
        val url = "$apiRootURL/flatten/$path"
        return try {
            client.get<List<CollectionItem.Song>>(url)
        } catch (e: Exception) {
            println("Error flattening url: $url: $e")
            null
        }
    }

    override suspend fun getAlbums(url: String): List<CollectionItem>? {
        return try {
            client.get<List<CollectionItem.Directory>>(url)
        } catch (e: Exception) {
            println("Error getting albums url: $url: $e")
            null
        }
    }

    override suspend fun setLastFmNowPlaying(path: String): Boolean {
        val url = "$apiRootURL/lastfm/now_playing/$path"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error setting last fm now playing url: $url: $e")
            false
        }
    }

    override suspend fun scrobbleOnLastFm(path: String): Boolean {
        val url = "$apiRootURL/lastfm/scrobble/$path"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error scrobbling on last fm url: $url: $e")
            false
        }
    }
}
