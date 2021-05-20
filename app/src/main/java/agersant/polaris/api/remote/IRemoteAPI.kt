package agersant.polaris.api.remote

import agersant.polaris.api.IPolarisAPI
import agersant.polaris.api.ItemsCallback
import agersant.polaris.api.ThumbnailSize
import android.net.Uri
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

interface IRemoteAPI : IPolarisAPI {

    fun getRandomAlbums(handlers: ItemsCallback)

    fun getRecentAlbums(handlers: ItemsCallback)

    fun setLastFMNowPlaying(path: String)

    fun scrobbleOnLastFM(path: String)

    fun getAudioUri(path: String): Uri?

    fun getThumbnailUri(path: String, size: ThumbnailSize): Uri?

    @Throws(IOException::class)
    fun getThumbnail(path: String, size: ThumbnailSize): InputStream?
}
