package ru.magnat.smnavigator.map;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.data.DbHelper;
import ru.magnat.smnavigator.map.geofence.Geofenceable;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Store;
import android.accounts.Account;
import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class LocationHelper {
	
	private static LocationHelper sInstance;
	
	private Context mContext;
	private GoogleMap mMap;

	private ClusterManager<AbstractMarker> mClusterManager;
	
	private AccountHelper mAccountHelper;
	private Account mAccount;
	
	public synchronized static LocationHelper get(Context context, GoogleMap map) {
		if (sInstance == null) {
			sInstance = new LocationHelper(context, map);
		}
		
		return sInstance;
	}
	
	public synchronized static void destroy() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private Random random = new Random(System.currentTimeMillis()); 
	
	private static final int DISTRIBUTOR_STROKE_COLOR = Color.argb(200, 255, 0, 0);
	private static final int DISTRIBUTOR_STROKE_WIDTH = 7;
	private static final int DISTRIBUTOR_FILL_COLOR = Color.TRANSPARENT;
	
	private static final int DISTRIBUTOR_BRANCH_STROKE_COLOR = Color.argb(55, 255, 0, 0);
	private static final int DISTRIBUTOR_BRANCH_STROKE_WIDTH = 5;
	
	@SuppressWarnings("unused")
	private void addRegion() {
		DbHelper dbHelper = DbHelper.getInstance(mContext, mAccount);
		
		try {
			PolygonOptions polygonOptions1 = new PolygonOptions();
			polygonOptions1.strokeColor(DISTRIBUTOR_STROKE_COLOR);
			polygonOptions1.strokeWidth(DISTRIBUTOR_STROKE_WIDTH);
	        polygonOptions1.fillColor(DISTRIBUTOR_FILL_COLOR); 
	        polygonOptions1.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2000373929);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions1.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions2 = new PolygonOptions();
			polygonOptions2.strokeColor(DISTRIBUTOR_STROKE_COLOR);
			polygonOptions2.strokeWidth(DISTRIBUTOR_STROKE_WIDTH);
			polygonOptions2.fillColor(DISTRIBUTOR_FILL_COLOR);
	        polygonOptions2.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001585282);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions2.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions3 = new PolygonOptions();
			polygonOptions3.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions3.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
	        polygonOptions3.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
	        polygonOptions3.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001362249);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions3.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions4 = new PolygonOptions();
			polygonOptions4.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions4.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
	        polygonOptions4.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions4.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001362250);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions4.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions4);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions5 = new PolygonOptions();
			polygonOptions5.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions5.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions5.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions5.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001362251);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions5.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions5);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions6 = new PolygonOptions();
			polygonOptions6.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions6.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions6.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions6.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001362252);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions6.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions6);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions7 = new PolygonOptions();
			polygonOptions7.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions7.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions7.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions7.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001362253);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions7.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions7);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions8 = new PolygonOptions();
			polygonOptions8.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions8.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions8.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions8.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001601535);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions8.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions8);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions9 = new PolygonOptions();
			polygonOptions9.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions9.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions9.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions9.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001603041);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions9.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions9);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions10 = new PolygonOptions();
			polygonOptions10.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions10.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions10.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions10.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001603042);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions10.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions10);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions11 = new PolygonOptions();
			polygonOptions11.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions11.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions11.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions11.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001943499);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions11.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions11);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions12 = new PolygonOptions();
			polygonOptions12.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions12.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions12.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions12.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001943500);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions12.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions12);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions13 = new PolygonOptions();
			polygonOptions13.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions13.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions13.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions13.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001943501);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions13.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions14 = new PolygonOptions();
			polygonOptions14.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions14.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions14.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions14.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001943502);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions14.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions14);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions15 = new PolygonOptions();
			polygonOptions15.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions15.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions15.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions15.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2001943503);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions15.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions15);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions16 = new PolygonOptions();
			polygonOptions16.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions16.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions16.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions16.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002233847);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions16.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions16);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions17 = new PolygonOptions();
			polygonOptions17.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions17.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions17.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions17.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372660);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions17.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions17);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions18 = new PolygonOptions();
			polygonOptions18.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions18.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions18.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions18.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372661);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions18.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions18);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions19 = new PolygonOptions();
			polygonOptions19.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions19.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions19.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions19.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372662);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions19.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions19);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions20 = new PolygonOptions();
			polygonOptions20.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions20.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions20.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions20.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372663);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions20.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions20);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions21 = new PolygonOptions();
			polygonOptions21.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions21.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions21.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions21.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372664);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions21.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions21);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PolygonOptions polygonOptions22 = new PolygonOptions();
			polygonOptions22.strokeColor(DISTRIBUTOR_BRANCH_STROKE_COLOR);
			polygonOptions22.strokeWidth(DISTRIBUTOR_BRANCH_STROKE_WIDTH);
			polygonOptions22.fillColor(Color.argb(55, random.nextInt(255), random.nextInt(255), random.nextInt(255))); 
			polygonOptions22.geodesic(true);
	        
			List<Geofenceable> georegions = dbHelper.getGeoregionDao().queryForEq("id", 2002372665);
			
			for (Geofenceable georegion : georegions) {
				polygonOptions22.add(new LatLng(georegion.getLatitude(), georegion.getLongitude()));
			}
			
			mMap.addPolygon(polygonOptions22);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DbHelper.close();
	}
	
	private LocationHelper(Context context, GoogleMap map) {
		mContext = context; 
		mMap = map;
		
		mAccountHelper = AccountHelper.get(context);
		
		mAccount = mAccountHelper.getCurrentAccount();
	} 
	
	public void moveToPoint(double latitude, double longitude) {
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
	}
	
	@SuppressWarnings("unused")
	private void addHeatMap() {
		DbHelper dbHelper = DbHelper.getInstance(mContext, mAccount);
		
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
		
		DbHelper.close();
	}
	
	public void updateOverlays() {	
		mMap.clear();
		
		addPsrMarkers();
		addStoreMarkers();
		//addRegion();
		//addHeatMap();
	}
	
	private void addPsrMarkers() {
		DbHelper dbHelper = DbHelper.getInstance(mContext, mAccount);
		
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
		
		DbHelper.close();
	}
	
	private void addStoreMarkers() {
		DbHelper dbHelper = DbHelper.getInstance(mContext, mAccount);
		
		try {
			List<Store> stores = dbHelper.getStoreDao().queryBuilder().query();
			
			mClusterManager = new ClusterManager<AbstractMarker>(mContext, mMap);
			mClusterManager.setRenderer(new StoreClusterRenderer(mContext, mMap, mClusterManager));
			
			for (Store store : stores) {
				if (store.getLatitude() == 0 || store.getLongitude() == 0) continue;
				
				mClusterManager.addItem(new StoreMarker(store));  
			}
			
			mMap.setOnCameraChangeListener(mClusterManager);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DbHelper.close();
	}
	
}
