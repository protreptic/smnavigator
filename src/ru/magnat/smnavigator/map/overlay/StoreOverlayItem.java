package ru.magnat.smnavigator.map.overlay;

import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.util.Location;
import ru.magnat.smnavigator.util.Text;

import com.google.android.maps.OverlayItem;

public class StoreOverlayItem extends OverlayItem {

	public StoreOverlayItem(Store store) {
		super(Location.valueOf(store.getLatitude(), store.getLongitude()), Text.prepareAddress(store.getName()), Text.prepareAddress(store.getAddress()));
	} 

}
