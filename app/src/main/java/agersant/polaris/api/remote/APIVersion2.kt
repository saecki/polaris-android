package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.Directory
import agersant.polaris.Song
import agersant.polaris.api.ThumbnailSize
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class APIVersion2(
    downloadQueue: DownloadQueue,
    client: HttpClient,
    apiRootUrl: String,
) : APIBase(downloadQueue, client, apiRootUrl) {

    override fun getAudioUrl(path: String): String {
        return "$apiRootUrl/serve/$path"
    }

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        val serverAddress = apiRootUrl
        return "$serverAddress/serve/$path"
    }

    override suspend fun browse(path: String): List<CollectionItem>? {
        val url = "$apiRootUrl/browse/$path"
        return try {
            client.get(url)
        } catch (e: Exception) {
            println("Error browsing $url: $e")
            null
        }
    }

    override suspend fun flatten(path: String): List<Song>? {
        val url = "$apiRootUrl/flatten/$path"
        return try {
            client.get(url)
        } catch (e: Exception) {
            println("Error flattening $url: $e")
            null
        }
    }

    override suspend fun getAlbums(url: String): List<Directory>? {
        return try {
            client.get(url)
        } catch (e: Exception) {
            println("Error getting albums $url: $e")
            null
        }
    }

    override suspend fun setLastFmNowPlaying(path: String): Boolean {
        val url = "$apiRootUrl/lastfm/now_playing/$path"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error setting last fm now playing $url: $e")
            false
        }
    }

    override suspend fun scrobbleOnLastFm(path: String): Boolean {
        val url = "$apiRootUrl/lastfm/scrobble/$path"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error scrobbling on last fm $url: $e")
            false
        }
    }
}
