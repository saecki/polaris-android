package agersant.polaris.features.browse

import agersant.polaris.Playlist
import agersant.polaris.PolarisApplication
import agersant.polaris.api.PlaylistsCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel : ViewModel() {

    private val fetchCallback = object : PlaylistsCallback {
        override fun onSuccess(items: List<Playlist>) {
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
    private val mItems = MutableLiveData(listOf<Playlist>())
    private val mFetching = MutableLiveData(false)
    private val mError = MutableLiveData(false)

    val items: LiveData<List<Playlist>> = mItems
    val fetching: LiveData<Boolean> = mFetching
    val error: LiveData<Boolean> = mError
    val api = PolarisApplication.getState().api
    val playbackQueue = PolarisApplication.getState().playbackQueue

    fun loadPlaylists() {
        mFetching.value = true
        mError.value = false
        api.getPlaylists(fetchCallback)
    }
}
