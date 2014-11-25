package ru.magnat.smnavigator.activities;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.magnat.smnavigator.Application;
import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.db.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.LocationHelper;
import ru.magnat.smnavigator.map.overlay.StoreOverlay;
import ru.magnat.smnavigator.util.Apps;
import ru.magnat.smnavigator.util.Text;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {

	// Constants
    // Content provider authority
    public static final String AUTHORITY = "ru.magnat.smnavigator.auth";
    
    // Account
    public static final String ACCOUNT = "syncAccount";
    
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 45L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
	
    private Account mAccount;
    
	private MapView mMapView;
	private LocationHelper mLocationHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
		
		if (getIntent().getExtras() != null) {
			double latitude = getIntent().getExtras().getDouble("latitude");
			double longitude = getIntent().getExtras().getDouble("longitude");
			
			mLocationHelper.moveToPoint(latitude, longitude);
		} else {
			mLocationHelper.showMyself(); 
		}
		
		mAccount = Application.addSyncAccount(this);
		
	    //Turn on periodic syncing
	    ContentResolver.addPeriodicSync(mAccount, AUTHORITY, new Bundle(), SYNC_INTERVAL);
		
		addStoresOverlay();
	}
	
	private void addStoresOverlay() {
		try {
			List<Store> stores = MainDbHelper.getInstance(this).getStoreDao().queryForAll();
		
			if (stores.size() == 0) {
				return;
			}
		
			// Redraw the map
			mMapView.invalidate();
			
			// Getting list of overlays available in the map
			List<Overlay> mapOverlays = mMapView.getOverlays();
			
			// Remove store overlay from the map
			mapOverlays.remove(mStoreOverlay);
			
			mStoreOverlay = new StoreOverlay(mMapView.getResources().getDrawable(R.drawable.shop), mMapView);

			for (Store store : stores) {
				mStoreOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (store.getLatitude() * 1E6), (int) (store.getLongitude() * 1E6)), store.getName(), Text.prepareAddress(store.getAddress())));
			}
			
			// Add updated overlay to the map
			mapOverlays.add(mStoreOverlay);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private StoreOverlay mStoreOverlay;
	
	private void init() {
		getActionBar().setTitle(""); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype_small)); 
		
		// Getting reference to MapView
		mMapView = new MapView(this, getResources().getString(R.string.google_maps_api_key)); 
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		
		setContentView(mMapView);
		
		mLocationHelper = LocationHelper.getInstance(mMapView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		unregisterReceiver(mSyncReceiver); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mLocationHelper.startTracking();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mLocationHelper.stopTracking();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    
	    mRefreshItem = menu.findItem(R.id.refresh);
	    
	    return true;
	}
	
	private MenuItem mRefreshItem;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.showMyself: {
				mLocationHelper.showMyself(); 
			} break;
			case R.id.refresh: {
				// Pass the settings flags by inserting them in a bundle
		        Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		        // Request the sync for the default account, authority, and
		        // manual sync settings
		        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
			} break;
			case R.id.showObjects: {
				startActivity(new Intent(this, ObjectsActivity.class)); 
			} break;
			case R.id.about: {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(Apps.getVersionName(this)); 
				builder.setCancelable(true);
				builder.create().show();
			} break;
			case R.id.settings: {
				Intent intent = new Intent(this, SettingsActivity.class);
				
				startActivity(intent); 
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        Log.d("", "Broadcast received: " + intent.getAction());
	        
	        if(intent.getAction().equals(ACTION_SYNC)){
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started")) {
	            	Log.d("", "sync started");
	            	
	    			Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
	    			
	    			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout imageView = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

	    		    imageView.startAnimation(rotate);
	    		    imageView.setLayoutParams(new LayoutParams(64, 64)); 
	    			
	    		    if (mRefreshItem != null && mRefreshItem.getActionView() == null) {
	    		    	mRefreshItem.setActionView(imageView);
	    		    }
	            }
	            if (action.equals("completed")) {
	            	Log.d("", "sync completed");
	            	
	            	if (mRefreshItem != null && mRefreshItem.getActionView() != null) {
		    			mRefreshItem.getActionView().clearAnimation();
		    			mRefreshItem.setActionView(null);
		    			
		    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		    			
		    			mRefreshItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
		    			mRefreshItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            	}
	            }
	            if (action.equals("error")) {
	            	Log.d("", "sync error");
	            	
	            	if (mRefreshItem != null && mRefreshItem.getActionView() != null) {
		    			mRefreshItem.getActionView().clearAnimation();
		    			mRefreshItem.setActionView(null);
		    			 
		    			mRefreshItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_error));
		    		}
	            }
	        }
	    }
	    
	};
	
}