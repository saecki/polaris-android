package agersant.polaris.features.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import agersant.polaris.App;
import agersant.polaris.R;
import agersant.polaris.api.API;
import agersant.polaris.databinding.FragmentCollectionBinding;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding binding;
    private API api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.api = App.state.api;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCollectionBinding.inflate(inflater);

        binding.browseDirectories.setOnClickListener(this::browseDirectories);
        binding.randomAlbums.setOnClickListener(this::browseRandom);
        binding.recentlyAdded.setOnClickListener(this::browseRecent);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateButtons();
    }

    public void browseDirectories(View view) {
        NavController controller = Navigation.findNavController(view);
        controller.navigate(R.id.action_nav_collection_to_nav_browse);

        //Context context = view.getContext();
        //Intent intent = new Intent(context, BrowseFragment.class);
        //intent.putExtra(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.PATH);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //context.startActivity(intent);
    }

    public void browseRandom(View view) {
        NavController controller = Navigation.findNavController(view);
        controller.navigate(R.id.action_nav_collection_to_nav_browse);

        //Context context = view.getContext();
        //Intent intent = new Intent(context, BrowseFragment.class);
        //intent.putExtra(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.RANDOM);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //context.startActivity(intent);
    }

    public void browseRecent(View view) {
        NavController controller = Navigation.findNavController(view);
        controller.navigate(R.id.action_nav_collection_to_nav_browse);

        //Context context = view.getContext();
        //Intent intent = new Intent(context, BrowseFragment.class);
        //intent.putExtra(BrowseFragment.NAVIGATION_MODE, BrowseFragment.NavigationMode.RECENT);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //context.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtons();
    }

    private void updateButtons() {
        boolean isOffline = api.isOffline();
        binding.randomAlbums.setEnabled(!isOffline);
        binding.recentlyAdded.setEnabled(!isOffline);
    }
}
