package agersant.polaris.api

import agersant.polaris.CollectionItem
import agersant.polaris.Playlist

interface ItemsCallback {
    fun onSuccess(items: List<CollectionItem>)
    fun onError()
}

interface PlaylistsCallback {
    fun onSuccess(items: List<Playlist>)
    fun onError()
}
