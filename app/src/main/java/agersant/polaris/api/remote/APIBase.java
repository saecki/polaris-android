package agersant.polaris.api.remote;

import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;

import java.io.IOException;
import java.io.InputStream;

import agersant.polaris.CollectionItem;
import agersant.polaris.api.ItemsCallback;
import agersant.polaris.api.ThumbnailSize;
import okhttp3.Request;
import okhttp3.ResponseBody;

abstract class APIBase implements IRemoteAPI {

    private final DownloadQueue downloadQueue;
    final RequestQueue requestQueue;

    APIBase(DownloadQueue downloadQueue, RequestQueue requestQueue) {
        this.downloadQueue = downloadQueue;
        this.requestQueue = requestQueue;
    }

    abstract String getAudioURL(String path);

    abstract String getThumbnailURL(String path, ThumbnailSize size);

    public Uri getAudioUri(String path) {
        String url = getAudioURL(path);
        return Uri.parse(url);
    }

    public Uri getThumbnailUri(String path, ThumbnailSize size) {
        String url = getThumbnailURL(path, size);
        return Uri.parse(url);
    }

    public InputStream getThumbnail(String path, ThumbnailSize size) throws IOException {
        Request request = new Request.Builder().url(getThumbnailUri(path, size).toString()).build();
        ResponseBody body = requestQueue.requestSync(request);
        if (body != null) {
            return body.byteStream();
        } else {
            return null;
        }
    }

    abstract void getAlbums(String url, final ItemsCallback handlers);

    public void getRandomAlbums(ItemsCallback handlers) {
        String requestURL = ServerAPI.getAPIRootURL() + "/random/";
        getAlbums(requestURL, handlers);
    }

    public void getRecentAlbums(ItemsCallback handlers) {
        String requestURL = ServerAPI.getAPIRootURL() + "/recent/";
        getAlbums(requestURL, handlers);
    }

    public MediaSource getAudio(CollectionItem item) {
        return downloadQueue.getAudio(item);
    }

}
