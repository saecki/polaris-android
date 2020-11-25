package agersant.polaris

import android.app.Application
import android.content.Intent
import androidx.preference.PreferenceManager

class PolarisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        App.instance = this
        App.state = PolarisState(this)
        App.resources = resources
        App.preferences = PreferenceManager.getDefaultSharedPreferences(this)

        App.theme = App.Theme.tryFrom(App.preferences.getString(resources.getString(R.string.pref_key_theme), null))

        val playbackServiceIntent = Intent(this, PolarisPlaybackService::class.java)
        playbackServiceIntent.action = PolarisPlaybackService.APP_INTENT_COLD_BOOT
        startService(playbackServiceIntent)
        startService(Intent(this, PolarisDownloadService::class.java))

    }
}