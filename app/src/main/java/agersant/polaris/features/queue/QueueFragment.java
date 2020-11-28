package agersant.polaris.features.queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.Random;

import agersant.polaris.App;
import agersant.polaris.PlaybackQueue;
import agersant.polaris.PolarisPlayer;
import agersant.polaris.PolarisState;
import agersant.polaris.R;
import agersant.polaris.api.local.OfflineCache;
import agersant.polaris.api.remote.DownloadQueue;
import agersant.polaris.databinding.FragmentQueueBinding;


public class QueueFragment extends Fragment {

    private QueueAdapter adapter;
    private BroadcastReceiver receiver;
    private FragmentQueueBinding binding;
    private PolarisState state;

    private void subscribeToEvents() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackQueue.REMOVED_ITEM);
        filter.addAction(PlaybackQueue.REMOVED_ITEMS);
        filter.addAction(PlaybackQueue.QUEUED_ITEMS);
        filter.addAction(PolarisPlayer.OPENING_TRACK);
        filter.addAction(PolarisPlayer.PLAYING_TRACK);
        filter.addAction(OfflineCache.AUDIO_CACHED);
        filter.addAction(DownloadQueue.WORKLOAD_CHANGED);
        filter.addAction(OfflineCache.AUDIO_REMOVED_FROM_CACHE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                switch (intent.getAction()) {
                    case PlaybackQueue.REMOVED_ITEM:
                    case PlaybackQueue.REMOVED_ITEMS:
                        updateTutorial();
                        break;
                    case PlaybackQueue.QUEUED_ITEMS:
                    case PlaybackQueue.OVERWROTE_QUEUE:
                        adapter.notifyDataSetChanged();
                        updateTutorial();
                        break;
                    case PolarisPlayer.OPENING_TRACK:
                    case PolarisPlayer.PLAYING_TRACK:
                    case OfflineCache.AUDIO_CACHED:
                    case OfflineCache.AUDIO_REMOVED_FROM_CACHE:
                    case DownloadQueue.WORKLOAD_CHANGED:
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                        break;
                }
            }
        };
        App.instance.registerReceiver(receiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        state = App.state;
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentQueueBinding.inflate(inflater);

        adapter = new QueueAdapter(state);
        adapter.setTopPadding(App.appBarLayout.getHeight());
        binding.recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new QueueTouchCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        updateTutorial();

        return binding.getRoot();
    }

    private void updateTutorial() {
        boolean empty = adapter.getItemCount() == 0;
        if (empty) {
            binding.tutorial.setVisibility(View.VISIBLE);
        } else {
            binding.tutorial.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToEvents();
        updateTutorial();
    }

    @Override
    public void onStop() {
        super.onStop();
        App.instance.unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        updateOrderingIcon();
        updateTutorial();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_queue, menu);
        updateOrderingIcon();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_clear) {
            clear();
            return true;
        } else if (itemId == R.id.action_shuffle) {
            shuffle();
            return true;
        } else if (itemId == R.id.action_ordering_sequence || itemId == R.id.action_ordering_repeat_one || itemId == R.id.action_ordering_repeat_all) {
            setOrdering(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        int oldCount = adapter.getItemCount();
        state.playbackQueue.clear();
        adapter.notifyItemRangeRemoved(0, oldCount);
    }

    private void shuffle() {
        Random rng = new Random();
        int count = adapter.getItemCount();
        for (int i = 0; i <= count - 2; i++) {
            int j = i + rng.nextInt(count - i);
            state.playbackQueue.move(i, j);
            adapter.notifyItemMoved(i, j);
        }
    }

    private void setOrdering(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_ordering_sequence) {
            state.playbackQueue.setOrdering(PlaybackQueue.Ordering.SEQUENCE);
        } else if (itemId == R.id.action_ordering_repeat_one) {
            state.playbackQueue.setOrdering(PlaybackQueue.Ordering.REPEAT_ONE);
        } else if (itemId == R.id.action_ordering_repeat_all) {
            state.playbackQueue.setOrdering(PlaybackQueue.Ordering.REPEAT_ALL);
        }
        updateOrderingIcon();
    }

    private int getIconForOrdering(PlaybackQueue.Ordering ordering) {
        switch (ordering) {
            case REPEAT_ONE:
                return R.drawable.ic_repeat_one_white_24dp;
            case REPEAT_ALL:
                return R.drawable.ic_repeat_white_24dp;
            case SEQUENCE:
            default:
                return R.drawable.ic_reorder_white_24dp;
        }
    }

    private void updateOrderingIcon() {
        int icon = getIconForOrdering(state.playbackQueue.getOrdering());
        MenuItem orderingItem = App.toolbar.getMenu().findItem(R.id.action_ordering);
        if (orderingItem != null) {
            orderingItem.setIcon(icon);
        }
    }
}
