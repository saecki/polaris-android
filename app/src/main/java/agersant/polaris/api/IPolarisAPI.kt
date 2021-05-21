package agersant.polaris.api

import agersant.polaris.CollectionItem
import com.google.android.exoplayer2.source.MediaSource

interface IPolarisAPI {
    suspend fun getAudio(item: CollectionItem): MediaSource?

    suspend fun browse(path: String): List<CollectionItem>?

    suspend fun flatten(path: String): List<CollectionItem>?
}
