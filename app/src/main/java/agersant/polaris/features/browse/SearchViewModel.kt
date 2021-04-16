package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.PolarisApplication
import agersant.polaris.api.ItemsCallback
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    companion object {
        const val MIN_SEARCH_DELAY = 200
    }

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
    private val mFetching = MutableLiveData(false)
    private val mError = MutableLiveData(false)
    private val serverAPI = PolarisApplication.getState().serverAPI
    private var searchQuery = ""
    private var searchScheduled = false
    private val searchHandler = Handler()
    private val runSearch = Runnable {
        search(searchQuery)
        searchScheduled = false
    }
    private var lastSearchTime = 0L

    val items: LiveData<List<CollectionItem>> = mItems
    val fetching: LiveData<Boolean> = mFetching
    val error: LiveData<Boolean> = mError
    val api = PolarisApplication.getState().api
    val playbackQueue = PolarisApplication.getState().playbackQueue

    fun incSearch(query: String) {
        val currentTime = System.currentTimeMillis()

        val delay = lastSearchTime + MIN_SEARCH_DELAY - currentTime
        if (delay <= 0) {
            search(query)
        } else if (!searchScheduled) {
            searchHandler.postDelayed(runSearch, delay)
            searchScheduled = true
        }
        searchQuery = query

        mError.value = false
        lastSearchTime = System.currentTimeMillis()
    }

    private fun search(query: String) {
        mFetching.value = true
        serverAPI.search(query, fetchCallback)
    }
}
