package ru.magnat.smnavigator.activities;

import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.magnat.smnavigator.Application;
import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.map.LocationHelper;
import ru.magnat.smnavigator.util.Apps;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity {
	
	private LocationHelper mLocationHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); init(); Log.d("", "MainActivity.onCreate");

		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		mLocationHelper.updateOverlays();
	}
	
	private void init() {
		setContentView(R.layout.activity_main); 
		
		getActionBar().setTitle(""); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_action_view_as_grid)); 
		getActionBar().setHomeButtonEnabled(true); 
		
		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		mLocationHelper = LocationHelper.getInstance(this, map);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister receivers
		unregisterReceiver(mSyncReceiver); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    
	    mSyncItem = menu.findItem(R.id.actionSync);
	    
	    return true;
	}
	
	private MenuItem mSyncItem;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionLegend: {			
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(LayoutInflater.from(this).inflate(R.layout.legend_layout, new LinearLayout(this), false)); 
				builder.show();
			} break;
			case R.id.actionSync: {
				Application.sync();
			} break;
			case R.id.actionObjects: {
				startActivity(new Intent(this, ObjectsActivity.class)); 
			} break;
			case R.id.actionAbout: {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(Apps.getVersionName(this)); 
				builder.setCancelable(true);
				builder.create().show();
			} break;
			case R.id.actionSettings: {
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
	        if(intent.getAction().equals(ACTION_SYNC)) {
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d("", "sync started");
	            	
	    			Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
	    			
	    			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

	    		    view.startAnimation(rotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			
	    		    mSyncItem.setActionView(view);
	            }
	            if (action.equals("completed") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync completed"); Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			
	    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    			
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
	    			mSyncItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + " " + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            
	    			mLocationHelper.updateOverlays();
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
	
}