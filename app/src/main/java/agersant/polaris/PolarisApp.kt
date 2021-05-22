package agersant.polaris

import agersant.polaris.ui.Theme
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import kotlinx.coroutines.MainScope

class PolarisApp : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: PolarisApp
            private set

        @JvmStatic
        lateinit var state: PolarisState
            private set
    }

    val scope = MainScope()

    override fun onCreate() {
        super.onCreate()
        instance = this
        state = PolarisState(this)

        val playbackServiceIntent = Intent(this, PolarisPlaybackService::class.java)
            .setAction(PolarisPlaybackService.APP_INTENT_COLD_BOOT)
        startService(playbackServiceIntent)
        startService(Intent(this, PolarisDownloadService::class.java))

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeKey = resources.getString(R.string.pref_key_theme)
        setTheme(preferences.getString(themeKey, ""))
    }

    fun setTheme(value: String?) {
        val theme = try {
            Theme.valueOf(value!!)
        } catch (e: IllegalArgumentException) {
            Theme.System
        }
        when (theme) {
            Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Theme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
