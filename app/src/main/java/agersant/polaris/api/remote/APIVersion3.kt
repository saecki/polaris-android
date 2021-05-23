package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.Directory
import agersant.polaris.Song
import agersant.polaris.api.ThumbnailSize
import agersant.polaris.api.remote.ServerAPI.Companion.apiRootURL
import android.net.Uri
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal open class APIVersion3(
    downloadQueue: DownloadQueue,
    client: HttpClient,
) : APIBase(downloadQueue, client) {

    override fun getAudioUrl(path: String): String {
        return "$apiRootURL/serve/${Uri.encode(path)}"
    }

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        return "$apiRootURL/serve/${Uri.encode(path)}"
    }

    override suspend fun browse(path: String): List<CollectionItem>? {
        val url = "$apiRootURL/browse/${Uri.encode(path)}"
        return try {
            client.get(url)
        } catch (e: Exception) {
            println("Error browsing $url: $e")
            null
        }
    }

    override suspend fun flatten(path: String): List<Song>? {
        val url = "$apiRootURL/flatten/${Uri.encode(path)}"
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
        val url = "$apiRootURL/lastfm/now_playing/${Uri.encode(path)}"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error setting last fm now playing $url: $e")
            false
        }
    }

    override suspend fun scrobbleOnLastFm(path: String): Boolean {
        val url = "$apiRootURL/lastfm/scrobble/${Uri.encode(path)}"
        return try {
            val status = client.put<HttpStatusCode>(url)
            status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error scrobbling on last fm $url: $e")
            false
        }
    }
}
