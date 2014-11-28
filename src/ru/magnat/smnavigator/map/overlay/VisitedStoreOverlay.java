package ru.magnat.smnavigator.map.overlay;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.entities.Store;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class VisitedStoreOverlay extends BalloonItemizedOverlay<StoreOverlayItem> {

	private List<StoreOverlayItem> mOverlays = new ArrayList<StoreOverlayItem>();

	public VisitedStoreOverlay(MapView mapView, List<Store> stores) {
		super(boundCenter(mapView.getResources().getDrawable(R.drawable.visited)), mapView);
		
		for (Store store : stores) {
			mOverlays.add(new StoreOverlayItem(store)); 
			populate();
		}
	}

	@Override
	protected StoreOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, StoreOverlayItem item) {
		return true;
	}
	
}