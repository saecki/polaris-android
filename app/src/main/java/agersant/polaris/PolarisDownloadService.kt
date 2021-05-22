package agersant.polaris

import agersant.polaris.PolarisApp.Companion.state
import agersant.polaris.api.remote.DownloadQueue
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class PolarisDownloadService : LifecycleService() {
    private lateinit var downloadQueue: DownloadQueue
    private lateinit var timer: Timer

    override fun onCreate() {
        super.onCreate()
        val state = state
        downloadQueue = state.downloadQueue
        timer = Timer()
        timer.scheduleAtFixedRate(1500L, 500L) {
            lifecycleScope.launch {
                downloadQueue.downloadNext()
            }
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
