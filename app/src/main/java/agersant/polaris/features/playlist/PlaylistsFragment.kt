package agersant.polaris.features.playlist

import agersant.polaris.databinding.FragmentPlaylistsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout

class PlaylistsFragment : Fragment() {

    private val model: PlaylistsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipyRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: View
    private lateinit var errorRetry: View
    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPlaylistsBinding.inflate(inflater)

        recyclerView = binding.recyclerView
        swipeRefresh = binding.swipeRefresh
        progressBar = binding.progressBar
        errorMessage = binding.errorMessage.root
        errorRetry = binding.errorMessage.retry

        recyclerView.setHasFixedSize(true)

        adapter = PlaylistsAdapter(model.api, model.playbackQueue)
        recyclerView.adapter = adapter

        model.items.observe(viewLifecycleOwner, adapter::updateItems)
        model.fetching.observe(viewLifecycleOwner, this::updateFetching)
        model.error.observe(viewLifecycleOwner, errorMessage::isVisible::set)

        swipeRefresh.setOnRefreshListener { model.loadPlaylists() }
        errorRetry.setOnClickListener { model.loadPlaylists() }

        model.loadPlaylists()

        return binding.root
    }

    fun updateFetching(fetching: Boolean) {
        progressBar.isVisible = fetching
        swipeRefresh.isRefreshing = fetching
    }
}
