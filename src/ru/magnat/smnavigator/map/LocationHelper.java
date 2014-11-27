package ru.magnat.smnavigator.map;

import java.sql.SQLException;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.overlay.ManagerOverlay;
import ru.magnat.smnavigator.map.overlay.PsrOverlay;
import ru.magnat.smnavigator.map.overlay.StoreOverlay;
import ru.magnat.smnavigator.util.Text;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationHelper {
	
	private static LocationHelper sInstance;
	
	private MapView mMapView;
	private LocationManager mLocationManager;
	private MyLocationListener mMyLocationListener; 
	private String mBestProvider;
	private long mAccuracy;

	private Drawable shop;
	private Drawable psr;
	private Drawable manager;
	
	public static LocationHelper getInstance(MapView mapView) {
		if (sInstance == null) {
			sInstance = new LocationHelper(mapView);
		}
		
		return sInstance;
	}
	
	private LocationHelper(MapView mapView) {
		shop = mapView.getResources().getDrawable(R.drawable.shop);
		psr = mapView.getResources().getDrawable(R.drawable.psr);
		manager = mapView.getResources().getDrawable(R.drawable.manager);
		
		mLocationManager = (LocationManager) mapView.getContext().getSystemService(Context.LOCATION_SERVICE);

		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);

		mMapView = mapView;
		mMyLocationListener = new MyLocationListener(); 
	}
	
	public void startTracking() {
		mLocationManager.requestLocationUpdates(mBestProvider, 60 * 1000, mAccuracy, mMyLocationListener);
	}
	
	public void stopTracking() {
		mLocationManager.removeUpdates(mMyLocationListener); 
	}
	
	public void restartTracking() {
		stopTracking();
		startTracking();
	}
	
	public void moveToPoint(double latitude, double longitude, int zoom) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		MapController mapController = mMapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(zoom);
	}
	
	public void requestLocation() {
		Location location = mLocationManager.getLastKnownLocation(mBestProvider);
		
		if (location == null) {
			mLocationManager.requestSingleUpdate(mBestProvider, new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {}
				
				@Override
				public void onProviderEnabled(String provider) {}
				
				@Override
				public void onProviderDisabled(String provider) {}
				
				@Override
				public void onLocationChanged(Location location) {
					updateLocation(location);
					
					// Getting latitude
					double latitude = location.getLatitude();

					// Getting longitude
					double longitude = location.getLongitude();
					
					moveToPoint(latitude, longitude, 15);
				}
			}, null);
		} else {
			updateLocation(location);
			
			// Getting latitude
			double latitude = location.getLatitude();

			// Getting longitude
			double longitude = location.getLongitude();
			
			moveToPoint(latitude, longitude, 15);
		}
	}
	
	public void updateLocation(Location location) {
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
				
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		if (mapOverlays.contains(mManagerOverlay))
			mapOverlays.remove(mManagerOverlay);
		
		mManagerOverlay = getManagerOverlay(point);
		
		mapOverlays.add(mManagerOverlay);
		
		// Redraw the map
		mMapView.invalidate();
	}
	
	public void updateOverlays() {	
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		if (mapOverlays.contains(mStoreOverlay)) {
			mapOverlays.remove(mStoreOverlay);
		} mStoreOverlay = getStoreOverlay();
		mapOverlays.add(mStoreOverlay);
		
		if (mapOverlays.contains(mPsrOverlay)) {
			mapOverlays.remove(mStoreOverlay);
		} mPsrOverlay = getPsrOverlay();
		mapOverlays.add(mPsrOverlay);
		
		// Redraw the map
		mMapView.invalidate();
	}
	
	private ManagerOverlay mManagerOverlay;
	private PsrOverlay mPsrOverlay;
	private StoreOverlay mStoreOverlay;
	
	private ManagerOverlay getManagerOverlay(GeoPoint point) {
		ManagerOverlay managerOverlay = null;
		
		// Creating an instance of ItemizedOverlay to mark the current location
		// in the map
		managerOverlay = new ManagerOverlay(manager);

		// Creating an item to represent a mark in the overlay
		OverlayItem overlayItem = new OverlayItem(point, "", "");

		// Adding the mark to the overlay
		managerOverlay.addOverlay(overlayItem);
		
		return managerOverlay;
	}
	
	private PsrOverlay getPsrOverlay() {
		PsrOverlay psrOverlay = null;
		
		try {
			List<Psr> psrs = MainDbHelper.getInstance(mMapView.getContext()).getPsrDao().queryForAll();
			
			psrOverlay = new PsrOverlay(psr, mMapView);

			for (Psr psr : psrs) {
				psrOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (psr.getLatitude() * 1E6), (int) (psr.getLongitude() * 1E6)), psr.getName(), psr.getProject()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return psrOverlay;
	}
	
	private StoreOverlay getStoreOverlay() {
		StoreOverlay storeOverlay = null;
		
		try {
			List<Store> stores = MainDbHelper.getInstance(mMapView.getContext()).getStoreDao().queryForAll();
			
			storeOverlay = new StoreOverlay(shop, mMapView);

			for (Store store : stores) {
				storeOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (store.getLatitude() * 1E6), (int) (store.getLongitude() * 1E6)), store.getName(), Text.prepareAddress(store.getAddress())));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return storeOverlay;
	}
	
	public class MyLocationListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(mMapView.getContext(), mMapView.getResources().getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show();
		
	    	Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "disabled");
	    	
	    	mMapView.getContext().sendBroadcast(intent);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(mMapView.getContext(), mMapView.getResources().getString(R.string.locationAvailable), Toast.LENGTH_LONG).show();
	    	
			Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "enabled");
	    	
	    	mMapView.getContext().sendBroadcast(intent);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (provider.equals(mBestProvider)) { 
				switch (status) {
					case LocationProvider.AVAILABLE: {
						Log.d("", "available");
					} break;
					case LocationProvider.OUT_OF_SERVICE: {
						Log.d("", "out of service");
					} break;
					case LocationProvider.TEMPORARILY_UNAVAILABLE: {
						Log.d("", "temporary unavailable");
					} break;
					default: {
						Log.d("", "default");
					} break;
				}
			}
		}
		
	}
	
}
