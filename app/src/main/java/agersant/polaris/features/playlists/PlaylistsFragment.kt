package agersant.polaris.features.playlists

import agersant.polaris.databinding.FragmentPlaylistsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PlaylistsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentPlaylistsBinding.inflate(inflater)

        return binding.root
    }
}