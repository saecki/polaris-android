package agersant.polaris.features.collection.directories

import agersant.polaris.App
import agersant.polaris.CollectionItem
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
import java.util.*

class DirectoriesFragment : Fragment() {
    companion object {
        const val PATH = "PATH"
    }

    private val model: DirectoriesViewModel by viewModels()
    private lateinit var path: String

    private lateinit var binding: FragmentDirectoriesBinding
    private lateinit var fetchCallback: ItemsCallback
    private lateinit var adapter: BrowseAdapterExplorer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchCallback = object : ItemsCallback {
            override fun onSuccess(items: ArrayList<out CollectionItem?>) {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }
                model.items.postValue(items)
            }

            override fun onError() {
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                }
            }
        }

        path = arguments?.getString(PATH) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDirectoriesBinding.inflate(inflater)

        val callback: ItemTouchHelper.Callback = BrowseTouchCallback()
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        adapter = BrowseAdapterExplorer(App.state.api, App.state.playbackQueue)

        model.items.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.errorRetry.setOnClickListener { loadContent() }

        loadContent()

        return binding.root
    }

    private fun loadContent() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.GONE

        App.state.api.browse(path, fetchCallback)
    }
}