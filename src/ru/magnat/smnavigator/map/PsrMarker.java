package ru.magnat.smnavigator.map;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Psr;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PsrMarker extends AbstractMarker {

    public PsrMarker(Psr psr) {
        super(psr.getLatitude(), psr.getLongitude());
        
        setMarker(new MarkerOptions()
        	.position(new LatLng(psr.getLatitude(), psr.getLongitude()))
            .title(psr.getName())
            .snippet(psr.getProject()) 
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.psr)));
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