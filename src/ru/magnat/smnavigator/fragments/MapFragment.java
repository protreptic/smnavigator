package ru.magnat.smnavigator.fragments;

import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.account.AccountHelper;
import ru.magnat.smnavigator.account.AccountWrapper;
import ru.magnat.smnavigator.map.LocationHelper;
import ru.magnat.smnavigator.util.Apps;
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
import android.widget.Toast;

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
		
		if (mSyncItem.getActionView() != null) { 
			mSyncItem.getActionView().clearAnimation();
			mSyncItem.setActionView(null);
		}
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
				AccountHelper accountHelper = AccountHelper.getInstance(getActivity()); 
				
				Account account = accountHelper.getCurrentAccount();
				
				// Pass the settings flags by inserting them in a bundle
		        Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		        // Request the sync for the default account, authority, and
		        // manual sync settings
		        ContentResolver.requestSync(account, AccountWrapper.ACCOUNT_AUTHORITY, settingsBundle);
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
	        if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(AccountHelper.getInstance(getActivity()).getCurrentAccount().name)) { 
	        	Animation mRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate360);
	        	
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d("", "sync started");
	            	
	    			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getActivity()), false); 

	    		    view.startAnimation(mRotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			if (mSyncItem.getActionView() != null) { 
		    			mSyncItem.getActionView().clearAnimation();
		    			mSyncItem.setActionView(null);
	    			}
		    			
	    			mSyncItem.setActionView(view);
	            }
	            if (action.equals("ask") && mSyncItem != null) {
	            	Log.d("", "sync ask");
	            	
	    			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getActivity()), false); 

	    		    view.startAnimation(mRotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			
	    		    if (mSyncItem.getActionView() == null) {
	    		    	mSyncItem.setActionView(view);
	    		    }
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
