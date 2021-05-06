package agersant.polaris.features.browse

import agersant.polaris.CollectionItem
import agersant.polaris.databinding.FragmentSearchBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText

class SearchFragment : Fragment() {

    private val model: SearchViewModel by viewModels()
    private lateinit var searchField: TextInputEditText
    private lateinit var contentHolder: FrameLayout
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var errorMessage: LinearLayout
    private lateinit var errorRetry: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSearchBinding.inflate(inflater)
        searchField = binding.searchField
        contentHolder = binding.searchContentHolder
        progressBar = binding.progressBar
        errorMessage = binding.errorMessage.root
        errorRetry = binding.errorMessage.retry

        model.items.observe(viewLifecycleOwner, this::displayContent)
        model.fetching.observe(viewLifecycleOwner, progressBar::isVisible::set)
        model.error.observe(viewLifecycleOwner, errorMessage::isVisible::set)

        searchField.addTextChangedListener {
            model.incSearch(it.toString())
        }
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                true
            } else {
                false
            }
        }
        errorRetry.setOnClickListener {
            model.incSearch(searchField.text.toString())
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        searchField.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchField, InputMethodManager.SHOW_FORCED)
    }

    private fun displayContent(items: List<CollectionItem>) {
        val content: BrowseContent = when (getDisplayModeForItems(items)) {
            DisplayMode.EXPLORER -> BrowseContentExplorer(requireContext(), model.api, model.playbackQueue)
            DisplayMode.ALBUM -> BrowseContentAlbum(requireContext(), model.api, model.playbackQueue)
            DisplayMode.DISCOGRAPHY -> BrowseContentDiscography(requireContext(), model.api, model.playbackQueue)
        }
        content.updateItems(items)
        content.setOnRefreshListener(null)
        contentHolder.removeAllViews()
        contentHolder.addView(content.root)
    }

    private fun getDisplayModeForItems(items: List<CollectionItem>): DisplayMode {
        if (items.isEmpty()) {
            return DisplayMode.EXPLORER
        }
        val album = items[0].album
        var allDirectories = true
        var allHaveAlbum = album != null
        for (item in items) {
            allDirectories = allDirectories and item.isDirectory
            allHaveAlbum = allHaveAlbum and (item.album != null)
        }
        if (allDirectories && allHaveAlbum) {
            return DisplayMode.DISCOGRAPHY
        }

        return DisplayMode.EXPLORER
    }

    private enum class DisplayMode {
        EXPLORER,
        DISCOGRAPHY,
        ALBUM,
    }
}
