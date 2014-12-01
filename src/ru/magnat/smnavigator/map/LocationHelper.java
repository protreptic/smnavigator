package ru.magnat.smnavigator.map;

import java.sql.SQLException;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Manager;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.util.Text;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationHelper {
	
	private static LocationHelper sInstance;
	
	private Context mContext;
	private GoogleMap mMap;
	private LocationManager mLocationManager;
	private MyLocationListener mMyLocationListener; 
	private String mBestProvider;
	private long mAccuracy;

	public static LocationHelper getInstance(Context context, GoogleMap map) {
		if (sInstance == null) {
			sInstance = new LocationHelper(context, map);
		}
		
		return sInstance;
	}
	
	public static void destroy() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private LocationHelper(Context context, GoogleMap map) {
		mContext = context; mMap = map;
		
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);
		mMyLocationListener = new MyLocationListener(); 
	}
	
	public void startTracking() {
		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);
		mLocationManager.requestLocationUpdates(mBestProvider, 60 * 1000, mAccuracy, mMyLocationListener);
	}
	
	public void stopTracking() {
		mLocationManager.removeUpdates(mMyLocationListener); 
	}
	
	public void moveToPoint(double latitude, double longitude, int zoom) {
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
		mMap.moveCamera(CameraUpdateFactory.zoomBy(zoom));
	}
	
	public void requestLocation() {
		if (!mLocationManager.isProviderEnabled(mBestProvider)) {
			Toast.makeText(mContext, mContext.getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show();
			
	    	Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "disabled");
	    	
	    	mContext.sendBroadcast(intent);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.locationAvailable), Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "enabled");
	    	
	    	mContext.sendBroadcast(intent);
		}
		
		Location location = mLocationManager.getLastKnownLocation(mBestProvider);
		
		if (location == null) {
			Toast.makeText(mContext, mContext.getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show(); return;
		}  

		updateLocation(location);
		
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		moveToPoint(latitude, longitude, 15);
	}
	
	public void updateLocation(Location location) {	
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		Manager manager = new Manager();
		manager.setId(0);
		manager.setName(mContext.getString(R.string.manager)); 
		manager.setLatitude(latitude); 
		manager.setLongitude(longitude); 
		
		mMap.addMarker(new MarkerOptions()
        .position(new LatLng(manager.getLatitude(), manager.getLongitude())) 
        .title(manager.getName()) 
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.manager)));
	}
	
	public void updateOverlays() {	
		addPsrMarkers();
		addStoreMarkers();
	}
	
	private void addPsrMarkers() {
		MainDbHelper dbHelper = MainDbHelper.getInstance(mContext);
		
		try {
			List<Psr> psrs = dbHelper.getPsrDao().queryForAll();
			
			for (Psr psr : psrs) {
				mMap.addMarker(new MarkerOptions()
		        .position(new LatLng(psr.getLatitude(), psr.getLongitude())) 
		        .title(psr.getName()) 
		        .snippet(psr.getProject())
 		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.psr)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
	}
	
	private void addStoreMarkers() {
		MainDbHelper dbHelper = MainDbHelper.getInstance(mContext);
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryForAll();
			
			for (Store store : stores) {
				mMap.addMarker(new MarkerOptions()
		        .position(new LatLng(store.getLatitude(), store.getLongitude())) 
		        .title(Text.prepareAddress(store.getName()))  
		        .snippet(Text.prepareAddress(store.getAddress()))
		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shop)));
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
	}
	
	public class MyLocationListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(mContext, mContext.getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show();
			
	    	Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "disabled");
	    	
	    	mContext.sendBroadcast(intent);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(mContext, mContext.getString(R.string.locationAvailable), Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(MainActivity.ACTION_LOCATION);
	    	intent.putExtra("provider", "enabled");
	    	
	    	mContext.sendBroadcast(intent);
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
