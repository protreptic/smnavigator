package ru.magnat.smnavigator.location.cluster;

import ru.magnat.smnavigator.location.marker.AbstractMarker;
import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class StoreClusterRenderer extends DefaultClusterRenderer<AbstractMarker> {

	public StoreClusterRenderer(Context context, GoogleMap map, ClusterManager<AbstractMarker> clusterManager) {
		super(context, map, clusterManager);
	}

	@Override
	protected void onBeforeClusterItemRendered(AbstractMarker item, MarkerOptions markerOptions) {
		markerOptions.title(item.getMarker().getTitle());
		markerOptions.snippet(item.getMarker().getSnippet());
		markerOptions.icon(item.getMarker().getIcon());
	}

}
