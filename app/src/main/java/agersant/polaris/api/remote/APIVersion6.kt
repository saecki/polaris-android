package agersant.polaris.api.remote

import io.ktor.client.*

internal open class APIVersion6(
    downloadQueue: DownloadQueue,
    client: HttpClient,
) : APIVersion5(downloadQueue, client)
