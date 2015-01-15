package ru.magnat.smnavigator.location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.location.cluster.StoreClusterRenderer;
import ru.magnat.smnavigator.location.marker.AbstractMarker;
import ru.magnat.smnavigator.location.marker.StoreMarker;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Georegion;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

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
	
	public void moveCameraToLocation(double latitude, double longitude) {
		CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(latitude, longitude), 19);
		
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
	
	@SuppressWarnings("unused")
	private void addHeatMap() {
		SecuredStorage dbHelper = SecuredStorage.get(mContext, mAccount);
		
		try {
			List<LatLng> points = new ArrayList<LatLng>();
			List<Store> stores = dbHelper.getStoreDao().queryForAll();
			
			for (Store store : stores) {
				points.add(store.getPosition());
			}	
			
			if (!points.isEmpty()) {
			    HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
			        .data(points)
			        .build();
		   
			    mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
	}
	
	public void updateOverlays() {	
		mMap.clear();
		
		addPsrMarkers();
		addStoreMarkers();
		addBranchRegions();
		//addHeatMap();
	}
	
	private void addPsrMarkers() {
		SecuredStorage dbHelper = SecuredStorage.get(mContext, mAccount);
		
		try {
			List<Psr> psrs = dbHelper.getPsrDao().queryForAll();
			
			for (Psr psr : psrs) {
				if (psr.getLatitude() == 0 || psr.getLongitude() == 0) continue;
				
				mMap.addMarker(new MarkerOptions()
		        .position(new LatLng(psr.getLatitude(), psr.getLongitude())) 
		        .title(psr.getName()) 
		        .snippet(psr.getProject())
 		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.psr)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
	}
	
	private void addStoreMarkers() {
		SecuredStorage dbHelper = SecuredStorage.get(mContext, mAccount);
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryBuilder().query();
			
			mClusterManager = new ClusterManager<AbstractMarker>(mContext, mMap);
			mClusterManager.setRenderer(new StoreClusterRenderer(mContext, mMap, mClusterManager));
			mClusterManager.setOnClusterItemInfoWindowClickListener(new OnClusterItemInfoWindowClickListener<AbstractMarker>() {

				@Override
				public void onClusterItemInfoWindowClick(AbstractMarker item) {
					Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
				}
			});
			
			for (Store store : stores) {
				if (store.getLatitude() == 0 || store.getLongitude() == 0) continue;
				
				mClusterManager.addItem(new StoreMarker(store));  
			}
			
			mMap.setOnCameraChangeListener(mClusterManager);
			mMap.setOnMarkerClickListener(mClusterManager); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
	}
	
	private static final int GEOREGION_STROKE_COLOR = Color.argb(120, 255, 0, 0);
	private static final int GEOREGION_FILL_COLOR = Color.argb(45, 10, 200, 10);
	private static final int GEOREGION_STROKE_WIDTH = 8;
	
    private List<Branch> getBranches() {
    	List<Branch> branches = new ArrayList<Branch>();
    	
    	SecuredStorage dbHelper = SecuredStorage.get(mContext, mAccount);
		
		try {
			branches = dbHelper.getBranchDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
    	
    	return branches;
    }
	
	private void addBranchRegions() {
		for (Branch branch : getBranches()) {
			SecuredStorage dbHelper = SecuredStorage.get(mContext, mAccount);
			
			try {
				PolygonOptions polygonOptions = new PolygonOptions();
				polygonOptions.strokeColor(GEOREGION_STROKE_COLOR);
				polygonOptions.strokeWidth(GEOREGION_STROKE_WIDTH);
		        polygonOptions.fillColor(GEOREGION_FILL_COLOR); 
		        polygonOptions.geodesic(true);
		        
				List<Georegion> georegions = dbHelper.getGeoregionDao().queryForEq("branch_id", branch.getId());
				
				for (Georegion georegion : georegions) {
					polygonOptions.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
				}
				
				mMap.addPolygon(polygonOptions);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			SecuredStorage.close();
		}
	}
	
}
