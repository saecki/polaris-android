package agersant.polaris.features.settings

import agersant.polaris.App
import agersant.polaris.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        val space = inflater.inflate(R.layout.view_toolbar_spacing, root, false)
        val preferences = super.onCreateView(inflater, container, savedInstanceState)

        root.addView(space)
        root.addView(preferences)

        return root
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val passwordKey = resources.getString(R.string.pref_key_password)
        val passwordPreference = findPreference<EditTextPreference>(passwordKey)!!
        passwordPreference.setOnPreferenceChangeListener { _, newValue ->
            passwordPreference.summary = newValue.toString().replace(".".toRegex(), "*")
            true
        }
        passwordPreference.summary = passwordPreference.text.toString().replace(".".toRegex(), "*")

        val themeKey = resources.getString(R.string.pref_key_theme)
        val themePreference = findPreference<ListPreference>(themeKey)!!
        themePreference.setOnPreferenceChangeListener { _, newValue ->
            App.theme = App.Theme.tryFrom(newValue)
            true
        }
    }
}
