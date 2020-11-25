package agersant.polaris.features.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import agersant.polaris.App;
import agersant.polaris.CollectionItem;
import agersant.polaris.PlaybackQueue;
import agersant.polaris.PolarisPlayer;
import agersant.polaris.PolarisState;
import agersant.polaris.R;
import agersant.polaris.api.API;
import agersant.polaris.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private boolean seeking = false;
    private BroadcastReceiver receiver;
    private Handler seekBarUpdateHandler;
    private Runnable updateSeekBar;
    private API api;
    private PolarisPlayer player;
    private PlaybackQueue playbackQueue;

    private void subscribeToEvents() {
        final PlayerFragment that = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(PolarisPlayer.PLAYING_TRACK);
        filter.addAction(PolarisPlayer.PAUSED_TRACK);
        filter.addAction(PolarisPlayer.RESUMED_TRACK);
        filter.addAction(PolarisPlayer.COMPLETED_TRACK);
        filter.addAction(PolarisPlayer.OPENING_TRACK);
        filter.addAction(PolarisPlayer.BUFFERING);
        filter.addAction(PolarisPlayer.NOT_BUFFERING);
        filter.addAction(PlaybackQueue.CHANGED_ORDERING);
        filter.addAction(PlaybackQueue.QUEUED_ITEM);
        filter.addAction(PlaybackQueue.QUEUED_ITEMS);
        filter.addAction(PlaybackQueue.REMOVED_ITEM);
        filter.addAction(PlaybackQueue.REMOVED_ITEMS);
        filter.addAction(PlaybackQueue.REORDERED_ITEMS);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case PolarisPlayer.OPENING_TRACK:
                    case PolarisPlayer.BUFFERING:
                    case PolarisPlayer.NOT_BUFFERING:
                        that.updateBuffering();
                    case PolarisPlayer.PLAYING_TRACK:
                        that.updateContent();
                        that.updateControls();
                        break;
                    case PolarisPlayer.PAUSED_TRACK:
                    case PolarisPlayer.RESUMED_TRACK:
                    case PolarisPlayer.COMPLETED_TRACK:
                    case PlaybackQueue.CHANGED_ORDERING:
                    case PlaybackQueue.REMOVED_ITEM:
                    case PlaybackQueue.REMOVED_ITEMS:
                    case PlaybackQueue.REORDERED_ITEMS:
                    case PlaybackQueue.QUEUED_ITEM:
                    case PlaybackQueue.QUEUED_ITEMS:
                    case PlaybackQueue.OVERWROTE_QUEUE:
                        that.updateControls();
                        break;
                }
            }
        };
        App.instance.registerReceiver(receiver, filter);
    }

    private void scheduleSeekBarUpdates() {
        updateSeekBar = () -> {
            if (!seeking) {
                int precision = 10000;
                float position = player.getPositionRelative();
                binding.seekBar.setMax(precision);
                binding.seekBar.setProgress((int) (precision * position));
            }
            seekBarUpdateHandler.postDelayed(updateSeekBar, 20/*ms*/);
        };
        seekBarUpdateHandler.post(updateSeekBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PolarisState state = App.state;
        api = state.api;
        player = state.player;
        playbackQueue = state.playbackQueue;
        seekBarUpdateHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int newPosition = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                newPosition = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                seeking = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekToRelative((float) newPosition / seekBar.getMax());
                seeking = false;
                updateControls();
            }
        });

        binding.skipNext.setOnClickListener((View) -> {
            player.skipNext();
        });
        binding.skipPrevious.setOnClickListener((View) -> {
            player.skipPrevious();
        });
        binding.pauseToggle.setOnClickListener((View) -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.resume();
            }
        });

        refresh();

        return binding.getRoot();
    }

    private void refresh() {
        updateContent();
        updateControls();
        updateBuffering();
    }

    @Override
    public void onStart() {
        subscribeToEvents();
        scheduleSeekBarUpdates();
        super.onStart();
    }

    @Override
    public void onStop() {
        App.instance.unregisterReceiver(receiver);
        receiver = null;
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void updateContent() {
        CollectionItem currentItem = player.getCurrentItem();
        if (currentItem != null) {
            populateWithTrack(currentItem);
        }
    }

    private void updateControls() {
        final float disabledAlpha = 0.2f;

        int playPauseIcon = player.isPlaying() ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp;
        binding.pauseToggle.setImageResource(playPauseIcon);
        binding.pauseToggle.setAlpha(player.isIdle() ? disabledAlpha : 1.f);

        if (playbackQueue.hasNextTrack(player.getCurrentItem())) {
            binding.skipNext.setClickable(true);
            binding.skipNext.setAlpha(1.0f);
        } else {
            binding.skipNext.setClickable(false);
            binding.skipNext.setAlpha(disabledAlpha);
        }

        if (playbackQueue.hasPreviousTrack(player.getCurrentItem())) {
            binding.skipPrevious.setClickable(true);
            binding.skipPrevious.setAlpha(1.0f);
        } else {
            binding.skipPrevious.setClickable(false);
            binding.skipPrevious.setAlpha(disabledAlpha);
        }
    }

    private void updateBuffering() {
        if (player.isOpeningSong()) {
            binding.buffering.setText(R.string.player_opening);
        } else if (player.isBuffering()) {
            binding.buffering.setText(R.string.player_buffering);
        }
        if (player.isPlaying() && (player.isOpeningSong() || player.isBuffering())) {
            binding.buffering.setVisibility(View.VISIBLE);
        } else {
            binding.buffering.setVisibility(View.INVISIBLE);
        }

    }

    private void populateWithTrack(CollectionItem item) {
        assert item != null;

        String title = item.getTitle();
        if (title != null) {
            binding.title.setText(title);
        }

        String artist = item.getArtist();
        if (artist != null) {
            binding.artist.setText(artist);
        }

        String album = item.getAlbum();
        if (album != null) {
            binding.album.setText(album);
        }

        String artworkPath = item.getArtwork();
        if (artworkPath != null) {
            api.loadImageIntoView(item, binding.artwork);
        }
    }
}
