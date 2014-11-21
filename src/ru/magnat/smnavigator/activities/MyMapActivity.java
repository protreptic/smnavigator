package ru.magnat.smnavigator.activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.map.LocationHelper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MyMapActivity extends MapActivity {

	private MapView mMapView;
	private LocationHelper mLocationHelper;
	
	public static List<Store> sStores = new ArrayList<Store>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(""); 
		
		new UpdateStoresTask().execute();
		
		init();
		
		setContentView(mMapView);
	}
	
	public class UpdateStoresTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog mProgressDialog;
		
		public UpdateStoresTask() {
			mProgressDialog = new ProgressDialog(MyMapActivity.this);
			mProgressDialog.setTitle("");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage("Loading");
			mProgressDialog.setIndeterminate(true); 
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL("http://sfs.magnat.ru:8081/sm_get_outlets");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				GetStoresHelper storesHelper = new GetStoresHelper();
				sStores = storesHelper.readJsonStream(urlConnection.getInputStream());
				urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.hide();
			
			mLocationHelper.restartTracking();
		}
		
	}
	
	private void init() {
		// Getting reference to MapView
		mMapView = new MapView(this, getResources().getString(R.string.google_maps_api_key)); 
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		
		mLocationHelper = LocationHelper.getInstance(this);
		mLocationHelper.setMapView(mMapView);
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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