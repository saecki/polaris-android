package agersant.polaris.features.about

import agersant.polaris.R
import agersant.polaris.databinding.FragmentAboutBinding
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentAboutBinding.inflate(inflater)

        val info = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        binding.version.text = info.versionName

        binding.polarisGithub.setOnClickListener {
            val uri = Uri.parse(resources.getString(R.string.url_polaris_github))
            requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        binding.polarisAndroidGithub.setOnClickListener {
            val uri = Uri.parse(resources.getString(R.string.url_polaris_android_github))
            requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        return binding.root
    }
}