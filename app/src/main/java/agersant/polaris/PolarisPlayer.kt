package agersant.polaris

import agersant.polaris.api.API
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PolarisPlayer internal constructor(
    private val context: Context,
    private val api: API,
    private val playbackQueue: PlaybackQueue
) : Player.EventListener {

    companion object {
        const val PLAYBACK_ERROR = "PLAYBACK_ERROR"
        const val PLAYING_TRACK = "PLAYING_TRACK"
        const val PAUSED_TRACK = "PAUSED_TRACK"
        const val RESUMED_TRACK = "RESUMED_TRACK"
        const val COMPLETED_TRACK = "COMPLETED_TRACK"
        const val OPENING_TRACK = "OPENING_TRACK"
        const val SEEKING_WITHIN_TRACK = "SEEKING_WITHIN_TRACK"
        const val BUFFERING = "BUFFERING"
        const val NOT_BUFFERING = "NOT_BUFFERING"
    }

    private val mediaPlayer: ExoPlayer = SimpleExoPlayer.Builder(context).build()
    private var mediaSource: MediaSource? = null
    private var loadAudioJob: Job? = null
    private var resumeProgress: Float
    var currentItem: CollectionItem? = null
        private set

    init {
        resumeProgress = -1f
        mediaPlayer.addListener(this)
        val filter = IntentFilter()
        filter.addAction(PlaybackQueue.NO_LONGER_EMPTY)
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PlaybackQueue.NO_LONGER_EMPTY -> if (isIdle || !isPlaying) {
                        skipNext()
                    }
                }
            }
        }
        context.registerReceiver(receiver, filter)
    }

    private fun startServices() {
        context.startService(Intent(context, PolarisPlaybackService::class.java))
        context.startService(Intent(context, PolarisDownloadService::class.java))
        context.startService(Intent(context, PolarisScrobbleService::class.java))
    }

    private fun broadcast(event: String) {
        context.sendBroadcast(Intent().setAction(event))
    }

    private fun stop() {
        loadAudioJob?.cancel()
        mediaPlayer.stop()
        seekToStart()
        mediaSource = null
        currentItem = null
    }

    fun play(item: CollectionItem) {
        startServices()
        resumeProgress = -1f
        if (currentItem != null && item.path == currentItem!!.path) {
            println("Restarting playback for: " + item.path)
            seekToStart()
            resume()
            return
        }

        println("Beginning playback for: " + item.path)
        stop()
        mediaPlayer.playWhenReady = true
        currentItem = item
        broadcast(OPENING_TRACK)

        loadAudioJob = PolarisApp.instance.scope.launch {
            val fetchedMediaSource = api.loadAudio(item)
            if (fetchedMediaSource != null) {
                try {
                    mediaSource = fetchedMediaSource
                    mediaPlayer.setMediaSource(fetchedMediaSource)
                    mediaPlayer.prepare()
                    broadcast(PLAYING_TRACK)
                } catch (e: Exception) {
                    println("Error while beginning media playback: $e")
                    broadcast(PLAYBACK_ERROR)
                }
            } else {
                println("Could not find audio for item: ${item.path}")
            }

            loadAudioJob = null
        }
    }

    fun isUsing(mediaSource: MediaSource): Boolean {
        return this.mediaSource === mediaSource
    }

    fun resume() {
        startServices()
        mediaPlayer.playWhenReady = true
        broadcast(RESUMED_TRACK)
    }

    fun pause() {
        mediaPlayer.playWhenReady = false
        broadcast(PAUSED_TRACK)
    }

    private fun advance(currentItem: CollectionItem?, delta: Int): Boolean {
        val newTrack = playbackQueue.getNextTrack(currentItem, delta)
        if (newTrack != null) {
            play(newTrack)
            return true
        }
        return false
    }

    fun skipPrevious() {
        val currentItem = currentItem
        advance(currentItem, -1)
    }

    fun skipNext(): Boolean {
        val currentItem = currentItem
        return advance(currentItem, 1)
    }

    val isIdle: Boolean
        get() = currentItem == null
    val isOpeningSong: Boolean
        get() = loadAudioJob != null
    val isPlaying: Boolean
        get() = mediaPlayer.playWhenReady
    val isBuffering: Boolean
        get() = mediaPlayer.playbackState == Player.STATE_BUFFERING

    private fun seekToStart() {
        resumeProgress = -1f
        mediaPlayer.seekTo(0)
    }

    fun seekToRelative(progress: Float) {
        broadcast(SEEKING_WITHIN_TRACK)
        resumeProgress = -1f
        if (progress == 0f) {
            mediaPlayer.seekTo(0)
            return
        }
        val duration = mediaPlayer.duration
        if (duration == C.TIME_UNSET) {
            resumeProgress = progress
            return
        }
        val position = (duration * progress).toLong()
        mediaPlayer.seekTo(position)
    }

    val positionRelative: Float
        get() {
            if (resumeProgress >= 0) {
                return resumeProgress
            }
            val position = currentPosition
            val duration = duration
            return position / duration
        }
    val currentPosition: Float
        get() {
            val position = mediaPlayer.currentPosition
            return if (position == C.TIME_UNSET) {
                0f
            } else position.toFloat()
        }
    val duration: Float
        get() {
            val duration = mediaPlayer.duration
            return if (duration == C.TIME_UNSET) {
                0f
            } else duration.toFloat()
        }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING) {
            broadcast(BUFFERING)
        } else {
            broadcast(NOT_BUFFERING)
        }

        @SuppressLint("SwitchIntDef")
        when (playbackState) {
            Player.STATE_READY -> if (resumeProgress > 0f) {
                seekToRelative(resumeProgress)
            }
            Player.STATE_ENDED -> {
                broadcast(COMPLETED_TRACK)
                if (!skipNext()) {
                    pause()
                    seekToStart()
                }
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        broadcast(PLAYBACK_ERROR)
    }
}
