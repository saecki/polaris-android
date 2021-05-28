package agersant.polaris.api.remote

import agersant.polaris.api.ThumbnailSize
import android.net.Uri
import io.ktor.client.*

internal open class APIVersion7(
    downloadQueue: DownloadQueue,
    client: HttpClient,
    apiRootUrl: String,
) : APIVersion6(downloadQueue, client, apiRootUrl) {

    override fun getThumbnailUrl(path: String, size: ThumbnailSize): String {
        return "$apiRootUrl/thumbnail/${Uri.encode(path)}?size=$size"
    }
}
