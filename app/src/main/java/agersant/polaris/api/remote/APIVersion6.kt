package agersant.polaris.api.remote

import agersant.polaris.api.ThumbnailSize
import android.net.Uri

internal class APIVersion6(
    downloadQueue: DownloadQueue,
    requestQueue: RequestQueue
) : APIVersion5(downloadQueue, requestQueue) {

    override fun getThumbnailURL(path: String, size: ThumbnailSize): String {
        val serverAddress = ServerAPI.aPIRootURL
        return "$serverAddress/thumbnail/${Uri.encode(path)}?size=$size"
    }
}
