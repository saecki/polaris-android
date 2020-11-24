package agersant.polaris.features.collection.directories

import agersant.polaris.App
import agersant.polaris.api.ItemsCallback
import agersant.polaris.databinding.FragmentDirectoriesBinding
import agersant.polaris.features.collection.BrowseAdapterExplorer
import agersant.polaris.features.collection.BrowseTouchCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper

class DirectoriesFragment : Fragment() {
    private val viewModel: DirectoriesViewmodel by viewModels()
    private lateinit var binding: FragmentDirectoriesBinding
    private lateinit var fetchCallback: ItemsCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val that = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDirectoriesBinding.inflate(inflater)

        val callback: ItemTouchHelper.Callback = BrowseTouchCallback()
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = BrowseAdapterExplorer(App.state.api, App.state.playbackQueue)

        binding.browseErrorRetry.setOnClickListener { loadContent() }

        return binding.root
    }

    private fun loadContent() {
        //Intent intent = App.instance.getIntent();
        //switch (navigationMode) {
        //    case PATH: {
        //        String path = intent.getStringExtra(BrowseFragment.PATH);
        //        if (path == null) {
        //            path = "";
        //        }
        //        loadPath(path);
        //        break;
        //    }
        //    case RANDOM: {
        //        loadRandom();
        //        break;
        //    }
        //    case RECENT: {
        //        loadRecent();
        //        break;
        //    }
        //}
    }
}