package ru.magnat.smnavigator.map;

import java.sql.SQLException;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Manager;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.overlay.ManagerOverlay;
import ru.magnat.smnavigator.map.overlay.PotentialStoreOverlay;
import ru.magnat.smnavigator.map.overlay.PsrOverlay;
import ru.magnat.smnavigator.map.overlay.StoreOverlay;
import ru.magnat.smnavigator.map.overlay.VisitedStoreOverlay;
import android.content.Context;
import android.content.Intent;
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

public class LocationHelper {
	
	private static LocationHelper sInstance;
	
	private MapView mMapView;
	private LocationManager mLocationManager;
	private MyLocationListener mMyLocationListener; 
	private String mBestProvider;
	private long mAccuracy;

	public static LocationHelper getInstance(MapView mapView) {
		if (sInstance == null) {
			sInstance = new LocationHelper(mapView);
		}
		
		return sInstance;
	}
	
	public static void destroy() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private LocationHelper(MapView mapView) {
		mLocationManager = (LocationManager) mapView.getContext().getSystemService(Context.LOCATION_SERVICE);
		
		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);

		mMapView = mapView;
		mMyLocationListener = new MyLocationListener(); 
	}
	
	public void startTracking() {
		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);
		mLocationManager.requestLocationUpdates(mBestProvider, 60 * 1000, mAccuracy, mMyLocationListener);
	}
	
	public void stopTracking() {
		mLocationManager.removeUpdates(mMyLocationListener); 
	}
	
	public void restartTracking() {
		stopTracking();
		startTracking();
	}
	
	public void openBalloon() {
		
	}
	
	public void moveToPoint(double latitude, double longitude, int zoom) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		MapController mapController = mMapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(zoom);
	}
	
	public void requestLocation() {
		if (!mLocationManager.isProviderEnabled(mBestProvider)) {
			Toast.makeText(mMapView.getContext(), mMapView.getResources().getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show();
			
	    	Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "disabled");
	    	
	    	mMapView.getContext().sendBroadcast(intent);
		} else {
			Toast.makeText(mMapView.getContext(), mMapView.getResources().getString(R.string.locationAvailable), Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "enabled");
	    	
	    	mMapView.getContext().sendBroadcast(intent);
		}
		
		Location location = mLocationManager.getLastKnownLocation(mBestProvider);
		
		if (location == null) {
			Toast.makeText(mMapView.getContext(), mMapView.getResources().getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show(); return;
		}  

		updateLocation(location);
		
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		moveToPoint(latitude, longitude, 15);
	}
	
	public void updateLocation(Location location) {	
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

		if (mapOverlays.contains(mManagerOverlay))
			mapOverlays.remove(mManagerOverlay);
		
		mManagerOverlay = getManagerOverlay(location);
		
		mapOverlays.add(mManagerOverlay);
		
		// Redraw the map
		mMapView.invalidate();
	}
	
	public void updateOverlays() {	
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();

//		if (mapOverlays.contains(mPotentialStoreOverlay)) {
//			mapOverlays.remove(mPotentialStoreOverlay);
//		} mPotentialStoreOverlay = getPotentialStoreOverlay();
//		mapOverlays.add(mPotentialStoreOverlay);
//		
//		if (mapOverlays.contains(mVisitedStoreOverlay)) {
//			mapOverlays.remove(mVisitedStoreOverlay);
//		} mVisitedStoreOverlay = getVisitedStoreOverlay();
//		mapOverlays.add(mVisitedStoreOverlay);
		
		if (mapOverlays.contains(mStoreOverlay)) {
			mapOverlays.remove(mStoreOverlay);
		} mStoreOverlay = getStoreOverlay();
		mapOverlays.add(mStoreOverlay);
		
		if (mapOverlays.contains(mPsrOverlay)) {
			mapOverlays.remove(mPsrOverlay);
		} mPsrOverlay = getPsrOverlay();
		mapOverlays.add(mPsrOverlay);
		
		// Redraw the map
		mMapView.invalidate();
	}
	
	private ManagerOverlay mManagerOverlay;
	private PsrOverlay mPsrOverlay;
	private StoreOverlay mStoreOverlay;
	private PotentialStoreOverlay mPotentialStoreOverlay;
	private VisitedStoreOverlay mVisitedStoreOverlay;
	
	private ManagerOverlay getManagerOverlay(Location location) {
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		Manager manager = new Manager();
		manager.setId(0);
		manager.setName(mMapView.getResources().getString(R.string.manager)); 
		manager.setLatitude(latitude); 
		manager.setLongitude(longitude); 
			
		return new ManagerOverlay(mMapView, manager);
	}
	
	private PsrOverlay getPsrOverlay() {
		PsrOverlay psrOverlay = null;
		
		MainDbHelper dbHelper = MainDbHelper.getInstance(mMapView.getContext());
		dbHelper.log();
		
		try {
			List<Psr> psrs = dbHelper.getPsrDao().queryForAll();
			
			psrOverlay = new PsrOverlay(mMapView, psrs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
		
		return psrOverlay;
	}
	
	private PotentialStoreOverlay getPotentialStoreOverlay() {
		PotentialStoreOverlay storeOverlay = null;
		
		MainDbHelper dbHelper = MainDbHelper.getInstance(mMapView.getContext());
		dbHelper.log();
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryForAll();
			
			storeOverlay = new PotentialStoreOverlay(mMapView, stores.subList(33, 45));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
		
		return storeOverlay;
	}
	
	private VisitedStoreOverlay getVisitedStoreOverlay() {
		VisitedStoreOverlay storeOverlay = null;
		
		MainDbHelper dbHelper = MainDbHelper.getInstance(mMapView.getContext());
		dbHelper.log();
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryForAll();
			
			storeOverlay = new VisitedStoreOverlay(mMapView, stores.subList(26, 32));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
		
		return storeOverlay;
	}
	
	private StoreOverlay getStoreOverlay() {
		StoreOverlay storeOverlay = null;
		
		MainDbHelper dbHelper = MainDbHelper.getInstance(mMapView.getContext());
		dbHelper.log();
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryForAll();
		
			storeOverlay = new StoreOverlay(mMapView, stores);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
		
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
