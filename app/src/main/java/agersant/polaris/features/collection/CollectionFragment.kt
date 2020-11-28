package agersant.polaris.features.collection

import agersant.polaris.R
import agersant.polaris.databinding.FragmentCollectionBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class CollectionFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCollectionBinding.inflate(inflater)

        binding.directories.setOnClickListener(this::browseDirectories)
        binding.artists.setOnClickListener(this::browseArtist)
        binding.albums.setOnClickListener(this::browseAlbums)
        binding.genres.setOnClickListener(this::browseGenres)
        binding.random.setOnClickListener(this::browseRandom)
        binding.recent.setOnClickListener(this::browseRecent)

        return binding.root
    }

    private fun browseDirectories(view: View) {
        val controller = Navigation.findNavController(view)
        controller.navigate(R.id.action_nav_collection_to_nav_directories)
    }

    private fun browseArtist(view: View?) {
        //TODO
    }

    private fun browseAlbums(view: View?) {
        //TODO
    }

    private fun browseGenres(view: View?) {
        //TODO
    }

    private fun browseRandom(view: View?) {
        //TODO
    }

    private fun browseRecent(view: View?) {
        //TODO
    }
}