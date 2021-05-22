package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowseViewModel : ViewModel() {
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

    private fun setFetching() {
        mFetching.value = true
        mError.value = false
    }

    private fun updateItems(items: List<CollectionItem>?) {
        mItems.postValue(items ?: listOf())
        mError.postValue(items == null)
        mFetching.postValue(false)
    }

    fun loadPath(path: String) {
        setFetching()
        viewModelScope.launch(Dispatchers.IO) {
            updateItems(api.browse(path))
        }
    }

    fun loadRandom() {
        setFetching()
        viewModelScope.launch(Dispatchers.IO) {
            updateItems(serverAPI.getRandomAlbums())
        }
    }

    fun loadRecent() {
        setFetching()
        viewModelScope.launch(Dispatchers.IO) {
            updateItems(serverAPI.getRecentAlbums())
        }
    }
}
