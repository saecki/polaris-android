package agersant.polaris.features.collection.directories

import agersant.polaris.CollectionItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DirectoriesViewModel : ViewModel() {
    val items = MutableLiveData<ArrayList<out CollectionItem>>()
}