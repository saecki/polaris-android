package agersant.polaris

import agersant.polaris.PolarisApp.Companion.state
import agersant.polaris.api.remote.DownloadQueue
import android.content.Intent
import android.os.Binder
import androidx.lifecycle.LifecycleService
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class PolarisDownloadService : LifecycleService() {
    private inner class PolarisBinder : Binder()

    private lateinit var downloadQueue: DownloadQueue
    private lateinit var timer: Timer

    override fun onCreate() {
        super.onCreate()
        val state = state
        downloadQueue = state.downloadQueue
        timer = Timer()
        timer.scheduleAtFixedRate(1500L, 500L) {
            downloadQueue.downloadNext()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }
}
