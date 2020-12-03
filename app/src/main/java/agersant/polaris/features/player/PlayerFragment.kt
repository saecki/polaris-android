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
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment

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
            if (!seeking) {
                val precision = 10000
                val position = player.positionRelative
                binding.seekBar.max = precision
                binding.seekBar.progress = (precision * position).toInt()
            }
            seekBarUpdateHandler.postDelayed(updateSeekBar, 20 /*ms*/)
        }
        seekBarUpdateHandler.post(updateSeekBar)
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
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var newPosition = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                newPosition = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player.seekToRelative(newPosition.toFloat() / seekBar.max)
                seeking = false
                updateControls()
            }
        })
        binding.skipNext.setOnClickListener { View: View? -> player.skipNext() }
        binding.skipPrevious.setOnClickListener { View: View? -> player.skipPrevious() }
        binding.pauseToggle.setOnClickListener { View: View? ->
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
        val currentItem = player.currentItem
        currentItem?.let { populateWithTrack(it) }
    }

    private fun updateControls() {
        val disabledAlpha = 0.2f
        val playPauseIcon = if (player.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
        binding.pauseToggle.setImageResource(playPauseIcon)
        binding.pauseToggle.alpha = if (player.isIdle) disabledAlpha else 1f
        if (playbackQueue.hasNextTrack(player.currentItem)) {
            binding.skipNext.isClickable = true
            binding.skipNext.alpha = 1.0f
        } else {
            binding.skipNext.isClickable = false
            binding.skipNext.alpha = disabledAlpha
        }
        if (playbackQueue.hasPreviousTrack(player.currentItem)) {
            binding.skipPrevious.isClickable = true
            binding.skipPrevious.alpha = 1.0f
        } else {
            binding.skipPrevious.isClickable = false
            binding.skipPrevious.alpha = disabledAlpha
        }
    }

    private fun updateBuffering() {
        if (player.isOpeningSong) {
            binding.buffering.setText(R.string.player_opening)
        } else if (player.isBuffering) {
            binding.buffering.setText(R.string.player_buffering)
        }
        if (player.isPlaying && (player.isOpeningSong || player.isBuffering)) {
            binding.buffering.visibility = View.VISIBLE
        } else {
            binding.buffering.visibility = View.INVISIBLE
        }
    }

    private fun populateWithTrack(item: CollectionItem) {
        binding.title.text = item.title
        binding.artist.text = item.artist
        binding.album.text = item.album
        if (item.artwork != null) {
            api.loadImageIntoView(item, binding.artwork)
        } else {
            binding.artwork.setImageResource(R.drawable.launcher_icon_foreground)
        }
    }
}