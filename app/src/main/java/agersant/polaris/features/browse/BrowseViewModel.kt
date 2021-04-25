package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApp
import agersant.polaris.api.ItemsCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BrowseViewModel : ViewModel() {
    private val fetchCallback = object : ItemsCallback {
        override fun onSuccess(items: List<CollectionItem>) {
            mItems.postValue(items)
            mFetching.postValue(false)
            mError.postValue(false)
        }

        override fun onError() {
            mItems.postValue(ArrayList())
            mFetching.postValue(false)
            mError.postValue(true)
        }
    }
    private val mItems = MutableLiveData(listOf<CollectionItem>())
    private val mError = MutableLiveData(false)
    private val mFetching = MutableLiveData(false)

    val api = PolarisApp.state.api
    val serverAPI = PolarisApp.state.serverAPI
    val playbackQueue = PolarisApp.state.playbackQueue
    val items: LiveData<List<CollectionItem>> = mItems
    val fetching: LiveData<Boolean> = mFetching
    val error: LiveData<Boolean> = mError
    var initialCreation = true
    var scrollPosition = 0


    fun loadPath(path: String) {
        mFetching.value = true
        mError.value = false
        api.browse(path, fetchCallback)
    }

    fun loadRandom() {
        mFetching.value = true
        mError.value = false
        serverAPI.getRandomAlbums(fetchCallback)
    }

    fun loadRecent() {
        mFetching.value = true
        mError.value = false
        serverAPI.getRecentAlbums(fetchCallback)
    }
}
