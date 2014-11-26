package ru.magnat.smnavigator.map;

import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.map.overlay.PsrOverlay;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyLocationListener implements LocationListener {
	
	public MyLocationListener(MapView mapView) {
		mMapView = mapView;		
	}
	
	private MapView mMapView;
	private PsrOverlay mPsrOverlay;
	
	@Override
	public void onLocationChanged(Location location) {
		addPsrOverlay(location); 
	}
	
	private void addPsrOverlay(Location location) {
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();

		// Creating an instance of GeoPoint corresponding to latitude and
		// longitude
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
		
		// Redraw the map
		mMapView.invalidate();
		
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		// Clear Existing overlays in the map
		mapOverlays.remove(mPsrOverlay);
		
		// Creating an instance of ItemizedOverlay to mark the current location
		// in the map
		mPsrOverlay = new PsrOverlay(mMapView.getResources().getDrawable(R.drawable.user_suit));

		// Creating an item to represent a mark in the overlay
		OverlayItem currentLocation = new OverlayItem(point, "Current location", "latitude : " + latitude + ", longitude:" + longitude);

		// Adding the mark to the overlay
		mPsrOverlay.addOverlay(currentLocation);

		// Adding new overlay to map overlay
		mapOverlays.add(mPsrOverlay);
	}
	
	public void showMyself(LocationManager locationManager, String bestProvider) {
		Location location = locationManager.getLastKnownLocation(bestProvider);
		
		if (location == null) {
			Toast.makeText(mMapView.getContext(), mMapView.getContext().getResources().getString(R.string.locationUnknown), Toast.LENGTH_LONG).show(); 
			return;
		}
		
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();

		// Creating an instance of GeoPoint corresponding to latitude and
		// longitude
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		// Getting MapController
		MapController mapController = mMapView.getController();

		// Locating the Geographical point in the Map
		mapController.animateTo(point);

		// Applying a zoom
		mapController.setZoom(12);

		// Redraw the map
		mMapView.invalidate();
		
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		// Clear Existing overlays in the map
		mapOverlays.remove(mPsrOverlay);
		
		// Creating an instance of ItemizedOverlay to mark the current location
		// in the map
		mPsrOverlay = new PsrOverlay(mMapView.getResources().getDrawable(R.drawable.user_suit));

		// Creating an item to represent a mark in the overlay
		OverlayItem currentLocation = new OverlayItem(point, "Current location", "latitude : " + latitude + ", longitude:" + longitude);

		// Adding the mark to the overlay
		mPsrOverlay.addOverlay(currentLocation);

		// Adding new overlay to map overlay
		mapOverlays.add(mPsrOverlay);
	}
	
	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
}
