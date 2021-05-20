package agersant.polaris.api

import agersant.polaris.CollectionItem

interface ItemsCallback {
    fun onSuccess(items: List<CollectionItem>)
    fun onError()
}
