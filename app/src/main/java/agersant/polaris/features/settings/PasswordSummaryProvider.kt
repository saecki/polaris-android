package agersant.polaris.features.settings

import agersant.polaris.R
import androidx.preference.EditTextPreference
import androidx.preference.Preference

class PasswordSummaryProvider : Preference.SummaryProvider<EditTextPreference> {
    override fun provideSummary(preference: EditTextPreference?): CharSequence {
        val text = preference?.text?.replace(".".toRegex(), "*") ?: ""

        return if (text.isNotEmpty())
            text
        else
            preference?.context?.getString(R.string.not_set) ?: ""
    }
}