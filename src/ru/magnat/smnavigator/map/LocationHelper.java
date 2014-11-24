package ru.magnat.smnavigator.map;

import ru.magnat.smnavigator.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.maps.MapView;

public class LocationHelper implements OnSharedPreferenceChangeListener {
	
	private static LocationHelper sInstance;
	
	private MapView mMapView;
	private LocationManager mLocationManager;
	private MyLocationListener mMyLocationListener; 
	private Location mCurrentLocation;
	private String mBestProvider;
	private SharedPreferences mDefaultSharedPreferences;
	private long mUpdateInterval;
	private long mAccuracy;
	
	private LocationHelper(MapView mapView) {
		mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mapView.getContext());
		mDefaultSharedPreferences.registerOnSharedPreferenceChangeListener(this); 
		
		mLocationManager = (LocationManager) mapView.getContext().getSystemService(Context.LOCATION_SERVICE);

		mBestProvider = mLocationManager.getBestProvider(new Criteria(), true);

		mCurrentLocation = mLocationManager.getLastKnownLocation(mBestProvider);
		
		mMapView = mapView;
		mUpdateInterval = mDefaultSharedPreferences.getLong(mMapView.getResources().getString(R.string.preference_location_update_interval), 90);
		mMyLocationListener = new MyLocationListener(mMapView); 
	}
	
	public void startTracking() {
		if (mCurrentLocation != null) {
			mMyLocationListener.onLocationChanged(mCurrentLocation);
		}

		mLocationManager.requestLocationUpdates(mBestProvider, mUpdateInterval * 1000, mAccuracy, mMyLocationListener);
	}
	
	public void stopTracking() {
		if (mMyLocationListener != null) {
			mLocationManager.removeUpdates(mMyLocationListener); 
		}
	}
	
	public void restartTracking() {
		stopTracking();
		startTracking();
	}
	
	public void showMyself() {
		mMyLocationListener.showMyself(mLocationManager, mBestProvider);
	}
	
	public static LocationHelper getInstance(MapView mapView) {
		if (sInstance == null) {
			sInstance = new LocationHelper(mapView);
		}
		
		return sInstance;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d("Shared", "Shared preference changed");
		
		if (key.equals(mMapView.getResources().getString(R.string.preference_location_update_interval))) {
			mUpdateInterval = mDefaultSharedPreferences.getLong(mMapView.getResources().getString(R.string.preference_location_update_interval), 90);
		} else if (key.equals(mMapView.getResources().getString(R.string.preference_location_accuracy))) {
			mAccuracy = mDefaultSharedPreferences.getLong(mMapView.getResources().getString(R.string.preference_location_accuracy), 300);
		}
		
		restartTracking();
	}
	
}
