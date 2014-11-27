package ru.magnat.smnavigator.map.overlay;

import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.util.Location;

import com.google.android.maps.OverlayItem;

public class PsrOverlayItem extends OverlayItem {

	public PsrOverlayItem(Psr psr) {
		super(Location.valueOf(psr.getLatitude(), psr.getLongitude()), psr.getName(), psr.getProject());
	} 

}
