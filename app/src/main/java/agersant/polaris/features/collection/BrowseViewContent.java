package agersant.polaris.features.collection;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import agersant.polaris.CollectionItem;


abstract class BrowseViewContent extends FrameLayout {

    public BrowseViewContent(Context context) {
        super(context);
    }

    void setItems(ArrayList<? extends CollectionItem> items) {
    }

    void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
    }
}
