package ru.magnat.smnavigator.map;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.util.Text;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreMarker extends AbstractMarker {

    public StoreMarker(Store store) {
        super(store.getLatitude(), store.getLongitude());
        
        setMarker(new MarkerOptions()
        	.position(new LatLng(store.getLatitude(), store.getLongitude()))
            .title(store.getCustomer().getName())
            .snippet(Text.prepareAddress(store.getAddress())) 
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shop)));
    }

	@Override
	public String toString() {
		return null;
	}

	@Override
	public MarkerOptions getMarker() {
		return marker;
	}
    
}