package agersant.polaris.api.remote;

import android.net.Uri;

import agersant.polaris.api.ThumbnailSize;

class APIVersion5 extends APIVersion4 {
    APIVersion5(DownloadQueue downloadQueue, RequestQueue requestQueue) {
        super(downloadQueue, requestQueue);
    }

    @Override
    String getAudioURL(String path) {
        String serverAddress = ServerAPI.getAPIRootURL();
        return serverAddress + "/audio/" + Uri.encode(path);
    }

    @Override
    String getThumbnailURL(String path, ThumbnailSize size) {
        String serverAddress = ServerAPI.getAPIRootURL();
        return serverAddress + "/thumbnail/" + Uri.encode(path);
    }
}
