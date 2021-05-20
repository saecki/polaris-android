package agersant.polaris.api

import agersant.polaris.CollectionItem
import com.google.android.exoplayer2.source.MediaSource
import java.io.IOException

interface IPolarisAPI {
    @Throws(IOException::class)
    fun getAudio(item: CollectionItem): MediaSource?

    fun browse(path: String, handlers: ItemsCallback)

    fun flatten(path: String, handlers: ItemsCallback)
}
