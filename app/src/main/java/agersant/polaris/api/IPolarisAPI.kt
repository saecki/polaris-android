package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.Song
import com.google.android.exoplayer2.source.MediaSource

interface IPolarisAPI {
    suspend fun getAudio(item: Song): MediaSource?

    suspend fun browse(path: String): List<CollectionItem>?

    suspend fun flatten(path: String): List<Song>?
}
