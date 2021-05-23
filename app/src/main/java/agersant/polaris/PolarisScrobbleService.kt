package agersant.polaris

import agersant.polaris.PolarisApp.Companion.state
import agersant.polaris.api.API
import agersant.polaris.api.remote.ServerAPI
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PolarisScrobbleService : LifecycleService() {

    companion object {
        private const val TICK_DELAY = 5000 // ms
    }

    private lateinit var receiver: BroadcastReceiver
    private lateinit var tickHandler: Handler
    private lateinit var tickRunnable: Runnable
    private lateinit var api: API
    private lateinit var serverAPI: ServerAPI
    private lateinit var player: PolarisPlayer
    private var seekedWithinTrack = false
    private var scrobbledTrack = false

    override fun onCreate() {
        super.onCreate()
        val state = state
        api = state.api
        player = state.player
        serverAPI = state.serverAPI

        val filter = IntentFilter()
        filter.addAction(PolarisPlayer.PLAYING_TRACK)
        filter.addAction(PolarisPlayer.COMPLETED_TRACK)
        filter.addAction(PolarisPlayer.RESUMED_TRACK)
        filter.addAction(PolarisPlayer.SEEKING_WITHIN_TRACK)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PolarisPlayer.COMPLETED_TRACK -> {
                        seekedWithinTrack = false
                        scrobbledTrack = false
                    }
                    PolarisPlayer.PLAYING_TRACK -> {
                        seekedWithinTrack = false
                        scrobbledTrack = false
                        nowPlaying()
                    }
                    PolarisPlayer.RESUMED_TRACK -> nowPlaying()
                    PolarisPlayer.SEEKING_WITHIN_TRACK -> seekedWithinTrack = true
                }
            }
        }
        registerReceiver(receiver, filter)

        seekedWithinTrack = false
        scrobbledTrack = false
        tickRunnable = Runnable {
            tick()
            tickHandler.postDelayed(tickRunnable, TICK_DELAY.toLong())
        }
        tickHandler = Handler(Looper.getMainLooper())
        tickHandler.postDelayed(tickRunnable, TICK_DELAY.toLong())
        nowPlaying()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        tickHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    private fun nowPlaying() {
        if (api.isOffline) return
        val item = player.currentSong ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            serverAPI.setLastFmNowPlaying(item.path)
        }
    }

    private fun tick() {
        if (api.isOffline) return
        if (scrobbledTrack || seekedWithinTrack) return
        if (!player.isPlaying) return
        val item = player.currentSong ?: return

        val duration = player.duration / 1000 // in seconds
        val currentTime = player.currentPosition / 1000 // in seconds
        if (currentTime <= 0f || duration <= 0f) return

        val shouldScrobble = duration > 30 && (currentTime > duration / 2 || currentTime > 4 * 60)
        if (!shouldScrobble) return

        lifecycleScope.launch(Dispatchers.IO) {
            serverAPI.scrobbleOnLastFm(item.path)
        }
        scrobbledTrack = true
    }
}
