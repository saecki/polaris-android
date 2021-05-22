package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.Song

interface ItemsCallback {
    fun onSuccess(items: List<CollectionItem>)
    fun onError()
}

interface SongCallback {
    fun onSuccess(items: List<Song>)
    fun onError()
}
