package agersant.polaris

import agersant.polaris.api.API
import agersant.polaris.api.local.LocalAPI
import agersant.polaris.api.local.OfflineCache
import agersant.polaris.api.remote.DownloadQueue
import agersant.polaris.api.remote.ServerAPI
import android.content.Context

class PolarisState internal constructor(context: Context) {
    @JvmField
    val offlineCache: OfflineCache

    @JvmField
    val downloadQueue: DownloadQueue

    @JvmField
    val playbackQueue: PlaybackQueue

    @JvmField
    val player: PolarisPlayer

    @JvmField
    val serverAPI: ServerAPI

    @JvmField
    val api: API

    init {
        val localAPI = LocalAPI()
        serverAPI = ServerAPI(context)
        api = API(context)
        playbackQueue = PlaybackQueue()
        player = PolarisPlayer(context, api, playbackQueue)
        offlineCache = OfflineCache(context, playbackQueue, player)
        downloadQueue = DownloadQueue(context, api, playbackQueue, player, offlineCache, serverAPI)

        serverAPI.initialize(downloadQueue)
        localAPI.initialize(offlineCache)
        api.initialize(offlineCache, serverAPI, localAPI)
    }
}
