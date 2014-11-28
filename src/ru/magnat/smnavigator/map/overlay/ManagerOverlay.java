package ru.magnat.smnavigator.map.overlay;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.entities.Manager;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class ManagerOverlay extends BalloonItemizedOverlay<ManagerOverlayItem> {
	
	private Manager mManager;
	
	public ManagerOverlay(MapView mapView, Manager manager) {
		super(boundCenter(mapView.getResources().getDrawable(R.drawable.manager)), mapView);
		
		mManager = manager;
		populate();
	}

	@Override
	protected ManagerOverlayItem createItem(int arg0) {
		return new ManagerOverlayItem(mManager); 
	}

	@Override
	public int size() {
		return 1;
	}
	
}
