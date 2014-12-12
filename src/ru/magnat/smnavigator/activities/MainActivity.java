package ru.magnat.smnavigator.activities;

import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.account.AccountHelper;
import ru.magnat.smnavigator.account.AccountWrapper;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private FragmentTabHost mTabHost;
	
	private MenuItem mSyncItem;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle); 
		
		setContentView(R.layout.fragment_tab_host);
		
		AccountHelper accountHelper = AccountHelper.getInstance(this);
		
		Account account = accountHelper.getCurrentAccount();
		
		getActionBar().setTitle(account.name); 
		getActionBar().setSubtitle(account.type); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_action_view_as_grid)); 
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        
        mTabHost.addTab(mTabHost.newTabSpec("map").setIndicator(getString(R.string.titleMap), null), MapFragment.class, null); 
        mTabHost.addTab(mTabHost.newTabSpec("psrs").setIndicator(getString(R.string.titlePsrs), null), PsrListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("stores").setIndicator(getString(R.string.titleStores), null), StoreListFragment.class, null);
        
		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister receivers
		unregisterReceiver(mSyncReceiver); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu); 
		
		mSyncItem = menu.findItem(R.id.actionSync);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionSync: {
				AccountHelper accountHelper = AccountHelper.getInstance(this); 
				
				Account account = accountHelper.getCurrentAccount();
				
				// Pass the settings flags by inserting them in a bundle
		        Bundle settingsBundle = new Bundle();
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        
		        // Request the sync for the default account, authority, and
		        // manual sync settings
		        ContentResolver.requestSync(account, AccountWrapper.ACCOUNT_AUTHORITY, settingsBundle);
			} break;
//			case R.id.actionAbout: {
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setMessage(Apps.getVersionName(this)); 
//				builder.setCancelable(true);
//				builder.create().show();
//			} break;
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
	        if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(AccountHelper.getInstance(getBaseContext()).getCurrentAccount().name)) { 
	        	Animation mRotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
	        	
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d("", "sync started");
	            	
	    			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

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
	            	
	    			LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

	    		    view.startAnimation(mRotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			
	    		    if (mSyncItem.getActionView() == null) {
	    		    	mSyncItem.setActionView(view);
	    		    }
	            }
	            if (action.equals("completed") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d("", "sync completed"); Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			
	    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    			
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
	    			mSyncItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + " " + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            
	    			//mLocationHelper.updateOverlays();
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