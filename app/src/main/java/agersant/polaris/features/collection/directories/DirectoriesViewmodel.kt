package agersant.polaris.features.collection.directories

import agersant.polaris.CollectionItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DirectoriesViewmodel : ViewModel() {
    val songs = MutableLiveData<List<CollectionItem.Song>>()
}