package ru.magnat.smnavigator.fragments;

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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {
	
	private MenuItem mSyncItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// register receivers
		getActivity().registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		GoogleMap map = getMap();
		map.setMyLocationEnabled(true);
		
		mLocationHelper = LocationHelper.getInstance(getActivity(), map);
		mLocationHelper.updateOverlays();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		// unregister receivers
		getActivity().unregisterReceiver(mSyncReceiver); 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_fragment_menu, menu);
		
		mSyncItem = menu.findItem(R.id.actionSync);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionSync: {
				Application.sync();
			} break;
			case R.id.actionAbout: {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(Apps.getVersionName(getActivity())); 
				builder.setCancelable(true);
				builder.create().show();
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private LocationHelper mLocationHelper;
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_SYNC)) {
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d("", "sync started");
	            	
	    			Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate360);
	    			
	    			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getActivity()), false); 

	    		    view.startAnimation(rotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			
	    		    mSyncItem.setActionView(view);
	            }
	            if (action.equals("completed") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync completed"); Toast.makeText(getActivity(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			
	    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    			
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
	    			mSyncItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + " " + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            
	    			mLocationHelper.updateOverlays();
	            }
	            if (action.equals("error") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync error"); Toast.makeText(getActivity(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			 
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_error));
	            }
	        }
	    }
	    
	};
	
}
