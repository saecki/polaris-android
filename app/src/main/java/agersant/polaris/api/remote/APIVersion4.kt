package agersant.polaris.api.remote

import io.ktor.client.*

internal open class APIVersion4(
    downloadQueue: DownloadQueue,
    client: HttpClient
) : APIVersion3(downloadQueue, client)
