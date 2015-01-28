package ru.magnat.smnavigator.location.marker;

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
            .title("psr")
            .snippet(psr.getId().toString()) 
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.psr)));
    }

	@Override
	public MarkerOptions getMarker() {
		return marker;
	}
    
}