package agersant.polaris.features.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import agersant.polaris.App;
import agersant.polaris.CollectionItem;
import agersant.polaris.PlaybackQueue;
import agersant.polaris.PolarisState;
import agersant.polaris.api.API;
import agersant.polaris.api.ItemsCallback;
import agersant.polaris.api.remote.ServerAPI;
import agersant.polaris.databinding.FragmentBrowseBinding;


public class BrowseFragment extends Fragment {

    public static final String PATH = "PATH";
    public static final String NAVIGATION_MODE = "NAVIGATION_MODE";
    private FragmentBrowseBinding binding;
    private ItemsCallback fetchCallback;
    private NavigationMode navigationMode;
    private SwipeRefreshLayout.OnRefreshListener onRefresh;
    private ArrayList<? extends CollectionItem> items;
    private API api;
    private ServerAPI serverAPI;
    private PlaybackQueue playbackQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PolarisState state = App.state;
        api = state.api;
        serverAPI = state.serverAPI;
        playbackQueue = state.playbackQueue;

        final BrowseFragment that = this;
        fetchCallback = new ItemsCallback() {
            @Override
            public void onSuccess(final ArrayList<? extends CollectionItem> items) {
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    that.items = items;
                    that.displayContent();
                });
            }

            @Override
            public void onError() {
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.VISIBLE);
                });
            }
        };

        //Intent intent = App.instance.getIntent();
        //navigationMode = (NavigationMode) intent.getSerializableExtra(BrowseFragment.NAVIGATION_MODE);

        //if (navigationMode == NavigationMode.RANDOM) {
        //    onRefresh = (SwipeRefreshLayoutDirection direction) -> loadContent();
        //}

        //loadContent();
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrowseBinding.inflate(inflater);

        loadContent();

        return binding.getRoot();
    }

    private void loadContent() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.errorMessage.setVisibility(View.GONE);
        //Intent intent = App.instance.getIntent();
        //switch (navigationMode) {
        //    case PATH: {
        //        String path = intent.getStringExtra(BrowseFragment.PATH);
        //        if (path == null) {
        //            path = "";
        //        }
        //        loadPath(path);
        //        break;
        //    }
        //    case RANDOM: {
        //        loadRandom();
        //        break;
        //    }
        //    case RECENT: {
        //        loadRecent();
        //        break;
        //    }
        //}
    }

    @SuppressWarnings("UnusedParameters")
    public void retry(View view) {
        loadContent();
    }

    private void loadPath(String path) {
        api.browse(path, fetchCallback);
    }

    private void loadRandom() {
        serverAPI.getRandomAlbums(fetchCallback);
    }

    private void loadRecent() {
        serverAPI.getRecentAlbums(fetchCallback);
    }

    private DisplayMode getDisplayModeForItems(ArrayList<? extends CollectionItem> items) {
        if (items.isEmpty()) {
            return DisplayMode.EXPLORER;
        }

        String album = items.get(0).getAlbum();
        boolean allSongs = true;
        boolean allDirectories = true;
        boolean allHaveArtwork = true;
        boolean allHaveAlbum = album != null;
        boolean allSameAlbum = true;
        for (CollectionItem item : items) {
            allSongs &= !item.isDirectory();
            allDirectories &= item.isDirectory();
            allHaveArtwork &= item.getArtwork() != null;
            allHaveAlbum &= item.getAlbum() != null;
            allSameAlbum &= album != null && album.equals(item.getAlbum());
        }

        if (allDirectories && allHaveArtwork && allHaveAlbum) {
            return DisplayMode.DISCOGRAPHY;
        }

        if (album != null && allSongs && allSameAlbum) {
            return DisplayMode.ALBUM;
        }

        return DisplayMode.EXPLORER;
    }

    private enum DisplayMode {
        EXPLORER,
        DISCOGRAPHY,
        ALBUM,
    }

    enum NavigationMode {
        PATH,
        RANDOM,
        RECENT,
    }

    private void displayContent() {
        if (items == null) {
            return;
        }

        BrowseViewContent contentView = null;
        switch (getDisplayModeForItems(items)) {
            case EXPLORER:
                contentView = new BrowseViewExplorer(requireContext(), api, playbackQueue);
                break;
            case ALBUM:
                contentView = new BrowseViewAlbum(requireContext(), api, playbackQueue);
                break;
            case DISCOGRAPHY:
                contentView = new BrowseViewDiscography(requireContext(), api, playbackQueue);
                break;
        }

        contentView.setItems(items);
        contentView.setOnRefreshListener(onRefresh);

        binding.contentHolder.removeAllViews();
        binding.contentHolder.addView(contentView);
    }
}
