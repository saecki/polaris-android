package agersant.polaris.features.collection.directories

import agersant.polaris.App
import agersant.polaris.databinding.FragmentDirectoriesBinding
import agersant.polaris.features.collection.BrowseAdapterExplorer
import agersant.polaris.features.collection.BrowseTouchCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper

class DirectoriesFragment : Fragment() {
    companion object {
        const val PATH = "PATH"
    }

    private val model: DirectoriesViewModel by viewModels()

    private lateinit var binding: FragmentDirectoriesBinding
    private lateinit var adapter: BrowseAdapterExplorer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.path = arguments?.getString(PATH) ?: ""

        model.fetchUpdates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDirectoriesBinding.inflate(inflater)

        binding.recyclerView.setHasFixedSize(true)
        adapter = BrowseAdapterExplorer(App.state.api, App.state.playbackQueue)
        binding.recyclerView.adapter = adapter
        val callback: ItemTouchHelper.Callback = BrowseTouchCallback()
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.errorRetry.setOnClickListener { model.fetchUpdates() }

        model.items.observe(viewLifecycleOwner) { items ->
            adapter.items = items
        }
        model.fetching.observe(viewLifecycleOwner) { fetching ->
            if (fetching) binding.progressBar.show()
            else (binding.progressBar as ContentLoadingProgressBar).hide()
        }
        model.fetchingError.observe(viewLifecycleOwner) { error ->
            if (error) binding.errorMessage.visibility = View.VISIBLE
            else binding.errorMessage.visibility = View.GONE
        }

        return binding.root
    }
}