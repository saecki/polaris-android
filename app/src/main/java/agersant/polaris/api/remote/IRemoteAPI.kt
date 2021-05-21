package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.api.IPolarisAPI
import agersant.polaris.api.ThumbnailSize
import android.graphics.Bitmap
import android.net.Uri

interface IRemoteAPI : IPolarisAPI {

    suspend fun getRandomAlbums(): List<CollectionItem>?

    suspend fun getRecentAlbums(): List<CollectionItem>?

    suspend fun setLastFmNowPlaying(path: String): Boolean

    suspend fun scrobbleOnLastFm(path: String): Boolean

    suspend fun getAudioUri(path: String): Uri?

    suspend fun getThumbnail(path: String, size: ThumbnailSize): Bitmap?
}
