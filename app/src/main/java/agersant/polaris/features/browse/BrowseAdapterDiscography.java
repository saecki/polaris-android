package agersant.polaris.features.browse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import agersant.polaris.PolarisService;
import agersant.polaris.R;


class BrowseAdapterDiscography extends BrowseAdapter {

	BrowseAdapterDiscography(PolarisService service) {
		super(service);
	}

	@Override
	public BrowseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemQueueStatusView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_browse_item_queued, parent, false);
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_browse_discography_item, parent, false);
		return new BrowseItemHolderDiscography(service, this, itemView, itemQueueStatusView);
	}

}
