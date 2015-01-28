package ru.magnat.smnavigator.location.marker;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreMarker extends AbstractMarker {

    public StoreMarker(Store store) {
        super(store.getLatitude(), store.getLongitude());
        
        int icon;
        
        if (store.getStoreProperty().getIsActive()) {
        	icon = R.drawable.store;
        } else if (store.getStoreProperty().getIsPotential()) {
        	icon = R.drawable.shop_closed;
        } else if (store.getStoreProperty().getIsVisited()) {
        	icon = R.drawable.shop_open;
        } else {
        	icon = R.drawable.store;
        }
        
        setMarker(new MarkerOptions()
        	.position(new LatLng(store.getLatitude(), store.getLongitude()))
            .title("store")
            .snippet(store.getId().toString()) 
            .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

	@Override
	public MarkerOptions getMarker() {
		return marker;
	}
	
}