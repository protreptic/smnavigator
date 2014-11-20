package ru.magnat.smnavigator.map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MyMapActivity;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.overlay.PsrOverlay;
import ru.magnat.smnavigator.map.overlay.StoreOverlay;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d("Shared", "Location changed!");
		
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();

		// Creating an instance of GeoPoint corresponding to latitude and
		// longitude
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		// Getting MapController
		//MapController mapController = mMapView.getController();

		// Locating the Geographical point in the Map
		//mapController.animateTo(point);

		// Applying a zoom
		//mapController.setZoom(15);

		// Redraw the map
		mMapView.invalidate();
		
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		// Creating an instance of ItemizedOverlay to mark the current location
		// in the map
		PsrOverlay currentLocationOverlay = new PsrOverlay(mMapView.getResources().getDrawable(R.drawable.sm));

		// Creating an item to represent a mark in the overlay
		OverlayItem currentLocation = new OverlayItem(point, "Current Location", "Latitude : " + latitude + ", Longitude:" + longitude);

		// Adding the mark to the overlay
		currentLocationOverlay.addOverlay(currentLocation);
		
		StoreOverlay storeOverlay = new StoreOverlay(mMapView.getResources().getDrawable(R.drawable.shop32));
		
		for (Store store : MyMapActivity.sStores) {
			storeOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (store.getLatitude() * 1E6), (int) (store.getLongitude() * 1E6)), store.getDescription(), ""));
		}
		
		// Clear Existing overlays in the map
		mapOverlays.clear();

		// Adding new overlay to map overlay
		mapOverlays.add(storeOverlay);
		mapOverlays.add(currentLocationOverlay);
	}
	
	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
}
