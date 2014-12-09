package ru.magnat.smnavigator.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public abstract class AbstractMarker implements ClusterItem  {
	
    protected double latitude;
    protected double longitude;

    protected MarkerOptions marker;

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    protected AbstractMarker(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    @Override
    public abstract String toString();

    public abstract MarkerOptions getMarker();

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

	private void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	private void setLongitude(double longitude) {
		this.longitude = longitude;
	}
    
}