package agersant.polaris

import android.app.Application
import android.content.Intent

class PolarisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        App.instance = this
        App.state = PolarisState(this)
        App.resources = resources

        val playbackServiceIntent = Intent(this, PolarisPlaybackService::class.java)
        playbackServiceIntent.action = PolarisPlaybackService.APP_INTENT_COLD_BOOT
        startService(playbackServiceIntent)
        startService(Intent(this, PolarisDownloadService::class.java))

    }
}