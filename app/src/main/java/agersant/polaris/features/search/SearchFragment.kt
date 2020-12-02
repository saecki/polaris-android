package agersant.polaris.features.search

import agersant.polaris.databinding.FragmentSearchBinding
import agersant.polaris.showKeyboard
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater)

        binding.searchBox.showKeyboard()

        return binding.root
    }
}