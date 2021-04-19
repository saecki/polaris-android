package agersant.polaris.features.browse;

import android.content.Context;
import android.widget.FrameLayout;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.List;

import agersant.polaris.CollectionItem;


abstract class BrowseViewContent extends FrameLayout {

    public BrowseViewContent(Context context) {
        super(context);
    }

    void setItems(List<? extends CollectionItem> items) {
    }

    void setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener listener) {
    }

    int getScrollPosition() {
        return 0;
    }

    void setScrollPosition(int position) {
    }
}
