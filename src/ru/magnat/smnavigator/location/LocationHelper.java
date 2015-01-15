package ru.magnat.smnavigator.location;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.magnat.smnavigator.location.cluster.StoreClusterRenderer;
import ru.magnat.smnavigator.location.marker.AbstractMarker;
import ru.magnat.smnavigator.location.marker.PsrMarker;
import ru.magnat.smnavigator.location.marker.StoreMarker;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Georegion;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.experimental.Geocoordinate;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.j256.ormlite.dao.Dao;

public class LocationHelper {
	
	private static LocationHelper sInstance;
	
	private Context mContext;
	private GoogleMap mMap;

	private ClusterManager<AbstractMarker> mClusterManager;
	
	private Account mAccount;
	
	public synchronized static LocationHelper get(Context context, GoogleMap map, Account account) {
		if (sInstance == null) {
			sInstance = new LocationHelper(context, map, account);
		}
		
		return sInstance;
	}
	
	public synchronized static void destroy() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private LocationHelper(Context context, GoogleMap map, Account account) {
		mContext = context; 
		mMap = map;
		mAccount = account;
	} 
	
	public void moveCameraToLocation(double latitude, double longitude, int zoom) {
		CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(latitude, longitude), zoom);
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void showShop(Store store) {
		CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(store.getLatitude(), store.getLongitude()), 19);
		
		Collection<Marker> storeMarkers = mClusterManager.getMarkerCollection().getMarkers();
		
		for (Marker marker : storeMarkers) {
			LatLng position =  marker.getPosition();
			
			if (store.getLatitude() == position.latitude && store.getLongitude() == position.longitude) {
				marker.showInfoWindow(); break;
			} 
		}
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void moveToPoint(double latitude, double longitude) {
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
	}
	
	public void updateOverlays() {	
		mMap.clear();
		
		new LoadPsrGeodata().execute();
		new LoadStoreGeodata().execute();
		new LoadRegionGeodata().execute();
	}
	
    private PolylineOptions managerTrack = new PolylineOptions();
    private Polyline polyline;
    
    private List<Geocoordinate> getGeocoordinates() {
    	List<Geocoordinate> geocoordinates = null;
    	
    	SecuredStorage dbHelper = new SecuredStorage(mContext, mAccount);
		
		try {		
			Dao<Geocoordinate, Integer> geocoordinateDao = dbHelper.getGeocoordinateDao();
			
			geocoordinates = geocoordinateDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dbHelper.closeConnection();
		
		return geocoordinates;
    }
    
    private void saveGeocoordinate(Location location) {
    	SecuredStorage dbHelper = new SecuredStorage(mContext, mAccount);
		
		try {		
			Geocoordinate geocoordinate = new Geocoordinate();
			geocoordinate.setLatitude(location.getLatitude());
			geocoordinate.setLongitude(location.getLongitude()); 
			
			Dao<Geocoordinate, Integer> geocoordinateDao = dbHelper.getGeocoordinateDao();
			geocoordinateDao.create(geocoordinate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		dbHelper.closeConnection();
    }
    
    @SuppressWarnings("unused")
	private void addManagerTrack() {
    	managerTrack.width(6);
    	managerTrack.color(Color.RED);
    	managerTrack.geodesic(true);
    	
    	for (Geocoordinate geocoordinate : getGeocoordinates()) {
    		managerTrack.add(new LatLng(geocoordinate.getLatitude(), geocoordinate.getLongitude()));
		}
    	
    	polyline = mMap.addPolyline(managerTrack);
    	
    	mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				if (location != null) {
					float accuracy = location.getAccuracy();
					double altitude = location.getAltitude();
					float bearing = location.getBearing();
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					String provider = location.getProvider();
					float speed = location.getSpeed();
					Timestamp time = new Timestamp(location.getTime());
					Timestamp elapsedRealtimeNanos = new Timestamp(location.getElapsedRealtimeNanos());
					
					Log.d("locationChanged", "location [accuracy=" + accuracy + ", altitude=" + altitude + ", bearing=" + bearing + ", latitude=" + latitude + ", longitude=" + longitude + ", provider=" + provider + ", speed=" + speed + ", time=" + time + ", elapsedRealtimeNanos=" + elapsedRealtimeNanos + ", extras=" + location.getExtras() + "]");
					
					if (location.getSpeed() == 0) {
						return; 
					}
					
					saveGeocoordinate(location); 
					
					managerTrack.add(new LatLng(location.getLatitude(), location.getLongitude()));
					
					if (polyline != null) 
						polyline.remove();
					
					polyline = mMap.addPolyline(managerTrack);
				}
			}
		});
    }
    
	private class LoadRegionGeodata extends AsyncTask<Void, Void, Void> {

		private final int GEOREGION_STROKE_COLOR = Color.argb(120, 255, 0, 0);
		private final int GEOREGION_FILL_COLOR = Color.argb(45, 10, 200, 10);
		private final int GEOREGION_STROKE_WIDTH = 8;
		
		private SecuredStorage mSecuredStorage;
		
		@Override
		protected void onPreExecute() {
			mSecuredStorage = new SecuredStorage(mContext, mAccount);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mBranchRegions = getBranchRegions();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSecuredStorage.closeConnection();
			
			addBranchRegions();
		}
		
		private List<PolygonOptions> mBranchRegions;
		
		private void addBranchRegions() {
			for (PolygonOptions polygonOptions : mBranchRegions) {
				mMap.addPolygon(polygonOptions);
			}
		}
		
	    private List<Branch> getBranches() {
	    	List<Branch> branches = new ArrayList<Branch>();
	    	
			try {
				branches = mSecuredStorage.getBranchDao().queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
	    	return branches;
	    }
		
		private List<PolygonOptions> getBranchRegions() {
			List<PolygonOptions> polygonOptions = new ArrayList<PolygonOptions>();
			
			for (Branch branch : getBranches()) {
				try {
					List<Georegion> georegions = mSecuredStorage.getGeoregionDao().queryForEq("branch_id", branch.getId());
					
					if (georegions.isEmpty()) continue;
					
					PolygonOptions polygon = new PolygonOptions();
					polygon.strokeColor(GEOREGION_STROKE_COLOR);
					polygon.strokeWidth(GEOREGION_STROKE_WIDTH);
					polygon.fillColor(GEOREGION_FILL_COLOR); 
					polygon.geodesic(true);
					
					for (Georegion georegion : georegions) {
						polygon.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
					}
					
					polygonOptions.add(polygon);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			return polygonOptions;
		}
		
	}
	
	private class LoadStoreGeodata extends AsyncTask<Void, Void, Void> {
		
		private SecuredStorage mSecuredStorage;
		private List<StoreMarker> mStoreMarkers;
		
		@Override
		protected void onPreExecute() {
			mSecuredStorage = new SecuredStorage(mContext, mAccount);
		}

		@Override
		protected Void doInBackground(Void... params) {
			mStoreMarkers = getStoreMarkers();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSecuredStorage.closeConnection();
			
			addStoreMarkers();
		}
		
	    private List<Store> getStores() {
	    	List<Store> stores = new ArrayList<Store>();
	    	
			try {
				stores = mSecuredStorage.getStoreDao().queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
	    	return stores;
	    }
		
		private void addStoreMarkers() {
			mClusterManager = new ClusterManager<AbstractMarker>(mContext, mMap);
			mClusterManager.setRenderer(new StoreClusterRenderer(mContext, mMap, mClusterManager));
			
			for (StoreMarker storeMarker : mStoreMarkers) {
				mClusterManager.addItem(storeMarker); 
			}
			
			mMap.setOnCameraChangeListener(mClusterManager);
			mMap.setOnMarkerClickListener(mClusterManager); 
		}
		
		private List<StoreMarker> getStoreMarkers() {
			List<StoreMarker> storeMarkers = new ArrayList<StoreMarker>();
			
			for (Store store : getStores()) {
				if (store.getLatitude() == 0 || store.getLongitude() == 0) continue;
				
				storeMarkers.add(new StoreMarker(store));
			}
			
			return storeMarkers;
		}
		
	}
	
	private class LoadPsrGeodata extends AsyncTask<Void, Void, Void> {

		private SecuredStorage mSecuredStorage;
		private List<PsrMarker> mPsrMarkers;
		
		@Override
		protected void onPreExecute() {
			mSecuredStorage = new SecuredStorage(mContext, mAccount);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mPsrMarkers = getPsrMarkers();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSecuredStorage.closeConnection();
			
			addPsrMarkers();
		}
		
	    private List<Psr> getPsrs() {
	    	List<Psr> psrs = new ArrayList<Psr>();
	    	
			try {
				psrs = mSecuredStorage.getPsrDao().queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
	    	return psrs;
	    }
		
		private List<PsrMarker> getPsrMarkers() {
			List<PsrMarker> psrMarkers = new ArrayList<PsrMarker>();
			
			for (Psr psr : getPsrs()) {
				if (psr.getLatitude() == 0 || psr.getLongitude() == 0) continue;
				
				psrMarkers.add(new PsrMarker(psr));
			}
			
			return psrMarkers;
		}
	    
		private void addPsrMarkers() {
			for (PsrMarker psrMarker : mPsrMarkers) {
				mMap.addMarker(psrMarker.getMarker());
			}
		}
		
	}
	
	
}
