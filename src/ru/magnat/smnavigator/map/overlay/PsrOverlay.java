package ru.magnat.smnavigator.map.overlay;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.entities.Psr;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class PsrOverlay extends BalloonItemizedOverlay<PsrOverlayItem> {

	private List<PsrOverlayItem> mOverlays = new ArrayList<PsrOverlayItem>();
	
	public PsrOverlay(MapView mapView, List<Psr> psrs) {
		super(boundCenter(mapView.getResources().getDrawable(R.drawable.psr)), mapView);
		
		for (Psr psr : psrs) {
			mOverlays.add(new PsrOverlayItem(psr)); 
		}
		
		populate();
	}

	@Override
	protected PsrOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, PsrOverlayItem item) {
		return true;
	}
	
}