package agersant.polaris.features.browse;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import agersant.polaris.CollectionItem;
import agersant.polaris.PlaybackQueue;
import agersant.polaris.R;
import agersant.polaris.Song;
import agersant.polaris.api.API;
import agersant.polaris.api.ThumbnailSize;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BrowseViewAlbum extends BrowseViewContent {

    private final BrowseAdapter adapter;
    private final ImageView artwork;
    private final TextView artist;
    private final TextView title;
    private final API api;

    public BrowseViewAlbum(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public BrowseViewAlbum(Context context, API api, PlaybackQueue playbackQueue) {
        super(context);
        this.api = api;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_browse_album, this, true);

        artwork = findViewById(R.id.album_artwork);
        artist = findViewById(R.id.album_artist);
        title = findViewById(R.id.album_title);

        RecyclerView recyclerView = findViewById(R.id.browse_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ItemTouchHelper.Callback callback = new BrowseTouchCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter = new BrowseAdapterAlbum(api, playbackQueue);
        recyclerView.setAdapter(adapter);
    }

    @Override
    void setItems(List<? extends CollectionItem> items) {
        var songs = (List<Song>) items;
        Collections.sort(songs, (a, b) -> {
            int discDifference = a.getDiscNumber() - b.getDiscNumber();
            if (discDifference != 0) {
                return discDifference;
            }
            return a.getTrackNumber() - b.getTrackNumber();
        });

        adapter.setItems(songs);

        var song = songs.get(0);

        String artworkPath = song.getArtwork();
        if (artworkPath != null) {
            api.loadThumbnailIntoView(song, ThumbnailSize.Small, artwork);
        }

        String titleString = song.getAlbum();
        if (title != null) {
            title.setText(titleString);
        }

        String artistString = song.getAlbumArtist();
        if (artistString == null) {
            artistString = song.getArtist();
        }
        if (artist != null) {
            artist.setText(artistString);
        }
    }

}
