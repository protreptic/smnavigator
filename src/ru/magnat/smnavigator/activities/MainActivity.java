package ru.magnat.smnavigator.activities;

import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.magnat.smnavigator.Application;
import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.map.LocationHelper;
import android.accounts.Account;
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
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {

	// Constants
    // Content provider authority
    public static final String AUTHORITY = "ru.magnat.smnavigator.auth";
    
    // Account
    public static final String ACCOUNT = "syncAccount";
    
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 10L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
	
    private Account mAccount;
    
	private MapView mMapView;
	private LocationHelper mLocationHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); init();

		// register receivers
		registerReceiver(mLocationReceiver, new IntentFilter(ACTION_LOCATION));
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		registerReceiver(mMoveReceiver, new IntentFilter(ACTION_MOVE));
		
		// request current location
		mLocationHelper.requestLocation();
		mLocationHelper.updateOverlays();
		
	    // turn on periodic sync
	    ContentResolver.addPeriodicSync(mAccount, AUTHORITY, new Bundle(), SYNC_INTERVAL);
	}
	
	private void init() {
		getActionBar().setTitle(""); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype_small)); 
		getActionBar().setHomeButtonEnabled(true); 
		
		// Getting reference to MapView
		mMapView = new MapView(this, getResources().getString(R.string.google_maps_api_key)); 
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		
		setContentView(mMapView);
		
		mLocationHelper = LocationHelper.getInstance(mMapView);
		mAccount = Application.addSyncAccount(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// start tracking
		mLocationHelper.startTracking();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// stop tracking
		mLocationHelper.stopTracking();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister receivers
		unregisterReceiver(mLocationReceiver);
		unregisterReceiver(mSyncReceiver); 
		unregisterReceiver(mMoveReceiver); 
		
		// turn off periodic sync
		ContentResolver.removePeriodicSync(mAccount, AUTHORITY, new Bundle());
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    
	    mSyncItem = menu.findItem(R.id.actionSync);
	    mLocationItem = menu.findItem(R.id.actionLocation);
	    
	    return true;
	}
	
	private MenuItem mSyncItem;
	private MenuItem mLocationItem;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionLocation: {
				mLocationHelper.requestLocation();
			} break;
			case R.id.actionSync: {
				// Pass the settings flags by inserting them in a bundle
		        Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		        // Request the sync for the default account, authority, and
		        // manual sync settings
		        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
			} break;
			case R.id.actionObjects: {
				startActivity(new Intent(this, ObjectsActivity.class)); 
			} break;
//			case R.id.actionAbout: {
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setMessage(Apps.getVersionName(this)); 
//				builder.setCancelable(true);
//				builder.create().show();
//			} break;
//			case R.id.actionSettings: {
//				Intent intent = new Intent(this, SettingsActivity.class);
//				
//				startActivity(intent); 
//			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public static final String ACTION_MOVE = "ru.magnat.smnavigator.sync.ACTION_MOVE";
	public static final String ACTION_LOCATION = "ru.magnat.smnavigator.sync.ACTION_LOCATION"; 
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_LOCATION)) {	        	
	            String action = intent.getStringExtra("provider");
	            
	            if (action.equals("disabled") && mLocationItem != null) {
	            	Log.d("", "location disabled");
	            	
	            	mLocationItem.setIcon(getResources().getDrawable(R.drawable.ic_action_location_found_error));
	            }
	            if (action.equals("enabled") && mLocationItem != null) {
	            	Log.d("", "location enabled");

	            	mLocationItem.setIcon(getResources().getDrawable(R.drawable.ic_action_location_found_ok));
	            }
	        }
		}
		
	};
	
	private BroadcastReceiver mMoveReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_MOVE)) {	        	
	        	double latitude = intent.getDoubleExtra("latitude", 0);
	        	double longitude = intent.getDoubleExtra("longitude", 0);
	        	
	        	int zoom = intent.getIntExtra("zoom", 15);
	        	
	        	Log.d("", "move " + latitude + " " + longitude + " " + zoom);
	        	
	            mLocationHelper.moveToPoint(latitude, longitude, zoom);
	        }
		}
		
	};
	
	private BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_SYNC)) {
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d("", "sync started");
	            	
	    			Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
	    			
	    			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

	    		    view.startAnimation(rotate);
	    		    view.setLayoutParams(new LayoutParams(64, 64)); 
	    			
	    		    mSyncItem.setActionView(view);
	    		    
	    		    
	            }
	            if (action.equals("completed") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync completed"); Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			
	    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    			
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
	    			mSyncItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + " " + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            }
	            if (action.equals("error") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync error"); Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			 
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_error));
	            }
	        }
	    }
	    
	};
	
	@Override
	public void onBackPressed() {
		Runtime.getRuntime().exit(1); 
	};
	
}