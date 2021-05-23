package agersant.polaris.features.settings

import agersant.polaris.PolarisApp.Companion.instance
import agersant.polaris.R
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val passwordKey = resources.getString(R.string.pref_key_password)
        val passwordPreference = findPreference<EditTextPreference>(passwordKey)
        passwordPreference!!.summaryProvider = PasswordSummaryProvider()

        val themeKey = resources.getString(R.string.pref_key_theme)
        val themePreference = findPreference<ListPreference>(themeKey)
        themePreference!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue != null) {
                    instance.setTheme(newValue.toString())
                }
                true
            }
    }
}
