package ru.magnat.smnavigator.map.overlay;

import ru.magnat.smnavigator.entities.Manager;
import ru.magnat.smnavigator.util.Location;

import com.google.android.maps.OverlayItem;

public class ManagerOverlayItem extends OverlayItem {

	public ManagerOverlayItem(Manager manager) {
		super(Location.valueOf(manager.getLatitude(), manager.getLongitude()), manager.getName(), "");
	} 

}
