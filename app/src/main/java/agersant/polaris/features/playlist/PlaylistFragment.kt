package agersant.polaris.features.playlist

import agersant.polaris.databinding.FragmentPlaylistsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PlaylistFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentPlaylistsBinding.inflate(inflater)

        return binding.root
    }
}