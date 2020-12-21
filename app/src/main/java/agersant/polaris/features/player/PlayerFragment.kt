package agersant.polaris.features.player

import agersant.polaris.*
import agersant.polaris.api.API
import agersant.polaris.databinding.FragmentPlayerBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class PlayerFragment : Fragment() {
    private var receiver: BroadcastReceiver? = null

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var seekBarUpdateHandler: Handler
    private lateinit var updateSeekBar: Runnable
    private lateinit var api: API
    private lateinit var player: PolarisPlayer
    private lateinit var playbackQueue: PlaybackQueue
    private var seeking = false

    private fun subscribeToEvents() {
        val that = this
        val filter = IntentFilter()
        filter.addAction(PolarisPlayer.PLAYING_TRACK)
        filter.addAction(PolarisPlayer.PAUSED_TRACK)
        filter.addAction(PolarisPlayer.RESUMED_TRACK)
        filter.addAction(PolarisPlayer.COMPLETED_TRACK)
        filter.addAction(PolarisPlayer.OPENING_TRACK)
        filter.addAction(PolarisPlayer.BUFFERING)
        filter.addAction(PolarisPlayer.NOT_BUFFERING)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PolarisPlayer.OPENING_TRACK, PolarisPlayer.BUFFERING, PolarisPlayer.NOT_BUFFERING -> {
                        that.updateBuffering()
                        that.updateContent()
                        that.updateControls()
                    }
                    PolarisPlayer.PLAYING_TRACK -> {
                        that.updateContent()
                        that.updateControls()
                    }
                    PolarisPlayer.PAUSED_TRACK,
                    PolarisPlayer.RESUMED_TRACK,
                    PolarisPlayer.COMPLETED_TRACK,
                    -> that.updateControls()
                }
            }
        }
        App.instance.registerReceiver(receiver, filter)
        playbackQueue.liveItems.observe(viewLifecycleOwner) { updateControls() }
        playbackQueue.liveOrdering.observe(viewLifecycleOwner) { updateControls() }
    }

    private fun scheduleSeekBarUpdates() {
        updateSeekBar = Runnable {
            val duration = player.duration / 1000
            val position = player.positionRelative
            updateTime(position, duration)
            seekBarUpdateHandler.postDelayed(updateSeekBar, 20 /*ms*/)
        }
        seekBarUpdateHandler.post(updateSeekBar)
    }

    private fun updateTime(position: Float, duration: Float) {
        val clamped = MathUtils.clamp(position, 0f, 1f)
        if (!seeking) binding.controls.slider.value = clamped
        binding.controls.currentTime.text = formatTime(clamped * duration)
        binding.controls.totalTime.text = formatTime(duration)
    }

    private fun formatTime(time: Float): String {
        val timeSeconds = if (time.isNaN()) 1 else time.roundToInt()
        val minutes = timeSeconds / 60
        val seconds = String.format("%02d", timeSeconds % 60)
        return "$minutes:$seconds"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = App.state
        api = state.api
        player = state.player
        playbackQueue = state.playbackQueue
        seekBarUpdateHandler = Handler()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        binding.controls.slider.setLabelFormatter { formatTime(it * player.duration / 1000) }
        binding.controls.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                seeking = true
            }

            override fun onStopTrackingTouch(slider: Slider) {
                player.seekToRelative(slider.value / slider.valueTo)
                seeking = false
                updateControls()
            }
        })
        binding.controls.skipNext.setOnClickListener { player.skipNext() }
        binding.controls.skipPrevious.setOnClickListener { player.skipPrevious() }
        binding.controls.pauseToggle.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.resume()
            }
        }
        refresh()
        return binding.root
    }

    private fun refresh() {
        updateContent()
        updateControls()
        updateBuffering()
    }

    override fun onStart() {
        subscribeToEvents()
        scheduleSeekBarUpdates()
        super.onStart()
    }

    override fun onStop() {
        App.instance.unregisterReceiver(receiver)
        receiver = null
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun updateContent() {
        populateWithTrack(player.currentItem)
    }

    private fun updateControls() {
        val disabledAlpha = 0.2f
        val playPauseIcon = if (player.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
        binding.controls.pauseToggle.setImageResource(playPauseIcon)
        binding.controls.pauseToggle.alpha = if (player.isIdle) disabledAlpha else 1f
        if (playbackQueue.hasNextTrack(player.currentItem)) {
            binding.controls.skipNext.isClickable = true
            binding.controls.skipNext.alpha = 1.0f
        } else {
            binding.controls.skipNext.isClickable = false
            binding.controls.skipNext.alpha = disabledAlpha
        }
        if (playbackQueue.hasPreviousTrack(player.currentItem)) {
            binding.controls.skipPrevious.isClickable = true
            binding.controls.skipPrevious.alpha = 1.0f
        } else {
            binding.controls.skipPrevious.isClickable = false
            binding.controls.skipPrevious.alpha = disabledAlpha
        }
    }

    private fun updateBuffering() {
        if (player.isPlaying && (player.isOpeningSong || player.isBuffering)) {
            binding.controls.loading.show()
        } else {
            binding.controls.loading.hide()
        }

        binding.controls.loading
    }

    private fun populateWithTrack(item: CollectionItem?) {
        binding.controls.title.text = item?.title ?: resources.getString(R.string.player_no_song)
        binding.controls.artist.text = item?.artist ?: resources.getString(R.string.player_unknown)
        binding.controls.album.text = item?.album ?: resources.getString(R.string.player_unknown)

        if (item?.artwork != null)
            api.loadImageIntoView(item, binding.artwork)
        else
            binding.artwork.setImageResource(R.drawable.launcher_icon_foreground)
    }
}