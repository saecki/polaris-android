package agersant.polaris.api.remote

import agersant.polaris.api.ThumbnailSize
import android.net.Uri
import io.ktor.client.*

internal open class APIVersion5(
    downloadQueue: DownloadQueue,
    client: HttpClient,
    apiRootUrl: String,
) : APIVersion4(downloadQueue, client, apiRootUrl) {

    override fun getAudioUrl(path: String): String {
        return "$apiRootUrl/audio/${Uri.encode(path)}"
    }

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        return "$apiRootUrl/thumbnail/${Uri.encode(path)}"
    }
}
