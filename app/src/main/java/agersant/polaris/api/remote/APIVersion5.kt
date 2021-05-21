package agersant.polaris.api.remote

import agersant.polaris.api.ThumbnailSize
import agersant.polaris.api.remote.ServerAPI.Companion.apiRootURL
import android.net.Uri
import io.ktor.client.*

internal open class APIVersion5(
    downloadQueue: DownloadQueue,
    client: HttpClient
) : APIVersion4(downloadQueue, client) {

    override fun getAudioUrl(path: String): String {
        return "$apiRootURL/audio/${Uri.encode(path)}"
    }

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        return "$apiRootURL/thumbnail/${Uri.encode(path)}"
    }
}
