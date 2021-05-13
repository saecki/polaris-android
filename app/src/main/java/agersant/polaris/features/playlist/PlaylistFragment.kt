package agersant.polaris.features.playlist

import agersant.polaris.Playlist
import agersant.polaris.databinding.FragmentPlaylistBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView

class PlaylistFragment : Fragment() {

    companion object {
        const val PLAYLIST = "PLAYLIST"
    }

    private val model: PlaylistViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: View
    private lateinit var errorRetry: View
    private lateinit var playlistName: TextView
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPlaylistBinding.inflate(inflater)
        recyclerView = binding.recyclerView
        progressBar = binding.progressBar
        errorMessage = binding.errorMessage.root
        errorRetry = binding.errorMessage.retry
        playlistName = binding.playlistName

        recyclerView.setHasFixedSize(true)

        adapter = PlaylistAdapter(model.api, model.playbackQueue)
        recyclerView.adapter = adapter

        model.items.observe(viewLifecycleOwner, adapter::updateItems)
        model.fetching.observe(viewLifecycleOwner, progressBar::isVisible::set)
        model.error.observe(viewLifecycleOwner, errorMessage::isVisible::set)

        errorRetry.setOnClickListener { model.loadPlaylist() }

        model.playlist = requireArguments().getSerializable(PLAYLIST) as Playlist

        model.loadPlaylist()
        playlistName.text = model.playlist.name

        return binding.root
    }
}
