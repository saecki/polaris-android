package agersant.polaris

import android.app.Application
import android.content.Intent

class PolarisApplication : Application() {
    companion object {
        private var instance: PolarisApplication? = null
        private var state: PolarisState? = null
        @JvmStatic
		fun getInstance(): PolarisApplication? {
            assert(instance != null)
            return instance
        }

        @JvmStatic
		fun getState(): PolarisState? {
            assert(state != null)
            return state
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        state = PolarisState(this)
        val playbackServiceIntent = Intent(this, PolarisPlaybackService::class.java)
        playbackServiceIntent.action = PolarisPlaybackService.APP_INTENT_COLD_BOOT
        startService(playbackServiceIntent)
        startService(Intent(this, PolarisDownloadService::class.java))
    }
}