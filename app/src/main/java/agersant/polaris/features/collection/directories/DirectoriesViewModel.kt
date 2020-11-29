package agersant.polaris.features.collection.directories

import agersant.polaris.App
import agersant.polaris.CollectionItem
import agersant.polaris.api.ItemsCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DirectoriesViewModel : ViewModel() {
    var path = ""
    val items = MutableLiveData<ArrayList<out CollectionItem>>()
    val fetching = MutableLiveData(false)
    val fetchingError = MutableLiveData(false)

    private val fetchCallback = object : ItemsCallback {
        override fun onSuccess(items: java.util.ArrayList<out CollectionItem?>) {
            this@DirectoriesViewModel.items.postValue(items)
            fetching.postValue(false)
            fetchingError.postValue(false)
        }

        override fun onError() {
            fetching.postValue(false)
            fetchingError.postValue(true)
        }
    }


    fun fetchUpdates() {
        fetching.value = true
        fetchingError.value = false
        App.state.api.browse(path, fetchCallback)
    }
}