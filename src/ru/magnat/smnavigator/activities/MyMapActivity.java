package ru.magnat.smnavigator.activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.Application;
import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.data.db.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.LocationHelper;
import ru.magnat.smnavigator.map.overlay.StoreOverlay;
import ru.magnat.smnavigator.util.Apps;
import ru.magnat.smnavigator.util.Text;
import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapActivity extends MapActivity {

	// Constants
    // Content provider authority
    public static final String AUTHORITY = "ru.magnat.smnavigator.auth";
    
    // Account
    public static final String ACCOUNT = "syncAccount";
    
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 45L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;
	
    private Account mAccount;
    
	private MapView mMapView;
	private LocationHelper mLocationHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();

		mLocationHelper.showMyself(); 
		
		mResolver = getContentResolver();
		
		mAccount = Application.addSyncAccount(this);
		
	    //Turn on periodic syncing
	    //ContentResolver.addPeriodicSync(mAccount, AUTHORITY, new Bundle(), SYNC_INTERVAL);
		
		addStoresOverlay();
	}
	
	public class UpdateStoresTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog mProgressDialog;
		
		public UpdateStoresTask() {
			mProgressDialog = new ProgressDialog(MyMapActivity.this);
			mProgressDialog.setTitle("");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage(getResources().getString(R.string.waiting));
			mProgressDialog.setIndeterminate(true);  
		}
		
		@Override
		protected void onPreExecute() {
			//mProgressDialog.show();
			
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout imageView = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, null);

		    Animation rotation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
		    imageView.startAnimation(rotation);
		    imageView.setLayoutParams(new LayoutParams(64, 64)); 
		    
		    mRefreshItem.setActionView(imageView);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL("http://sfs.magnat.ru:8081/sm_get_outlets");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
				for (Store store : new GetStoresHelper().readJsonStream(urlConnection.getInputStream())) {
					MainDbHelper.getInstance(MyMapActivity.this).getStoreDao().createOrUpdate(store);
				}

				urlConnection.disconnect();
				
				try {
					TimeUnit.SECONDS.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			//mProgressDialog.hide();

			mRefreshItem.getActionView().clearAnimation();
			mRefreshItem.setActionView(null);
			
			// Redraw the map
			mMapView.invalidate();
			
			// Getting list of overlays available in the map
			List<Overlay> mapOverlays = mMapView.getOverlays();
			
			// Remove store overlay from the map
			mapOverlays.remove(mStoreOverlay);
			
			mStoreOverlay = new StoreOverlay(mMapView.getResources().getDrawable(R.drawable.shop), mMapView);

			try {
				for (Store store : MainDbHelper.getInstance(mMapView.getContext()).getStoreDao().queryForAll()) {
					mStoreOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (store.getLatitude() * 1E6), (int) (store.getLongitude() * 1E6)), store.getName(), Text.prepareAddress(store.getAddress())));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Add updated overlay to the map
			mapOverlays.add(mStoreOverlay);
		}
		
	}
	
	private void addStoresOverlay() {
		// Redraw the map
		mMapView.invalidate();
		
		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mMapView.getOverlays();
		
		// Remove store overlay from the map
		mapOverlays.remove(mStoreOverlay);
		
		mStoreOverlay = new StoreOverlay(mMapView.getResources().getDrawable(R.drawable.shop), mMapView);

		try {
			for (Store store : MainDbHelper.getInstance(mMapView.getContext()).getStoreDao().queryForAll()) {
				mStoreOverlay.addOverlay(new OverlayItem(new GeoPoint((int) (store.getLatitude() * 1E6), (int) (store.getLongitude() * 1E6)), store.getName(), Text.prepareAddress(store.getAddress())));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Add updated overlay to the map
		mapOverlays.add(mStoreOverlay);
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
				mRefreshItem = item;
				
				new UpdateStoresTask().execute();
				
				// Pass the settings flags by inserting them in a bundle
		        //Bundle settingsBundle = new Bundle();
		        //settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        //settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		        // Request the sync for the default account, authority, and
		        // manual sync settings
		        //ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
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
	
}