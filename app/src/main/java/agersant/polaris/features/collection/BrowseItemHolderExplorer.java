package agersant.polaris.features.collection;

import android.view.View;

import com.google.android.material.textview.MaterialTextView;

import agersant.polaris.CollectionItem;
import agersant.polaris.PlaybackQueue;
import agersant.polaris.R;
import agersant.polaris.api.API;


class BrowseItemHolderExplorer extends BrowseItemHolder {

	private final MaterialTextView item;

	BrowseItemHolderExplorer(API api, PlaybackQueue playbackQueue, BrowseAdapter adapter, View itemView, View itemQueueStatusView) {
		super(api, playbackQueue, adapter, itemView, itemQueueStatusView);
		item = itemView.findViewById(R.id.browse_explorer_item);
		item.setOnClickListener(this);
	}

	@Override
	void bindItem(CollectionItem item) {
		super.bindItem(item);
		this.item.setText(item.getName());

		int icon;
		if (item.isDirectory()) {
			icon = R.drawable.ic_folder_open_black_24dp;
		} else {
			icon = R.drawable.ic_audiotrack_black_24dp;
		}

		this.item.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		this.item.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
	}

}
