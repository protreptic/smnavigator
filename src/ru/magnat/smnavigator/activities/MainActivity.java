package ru.magnat.smnavigator.activities;

import java.sql.SQLException;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Apps;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.AccountWrapper;
import ru.magnat.smnavigator.data.DbHelperSecured;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.update.UpdateHelper;
import android.accounts.Account;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private Manager getManager() {
    	Manager manager = null;
    	
    	DbHelperSecured dbHelper = DbHelperSecured.get(this, mAccount);
		
		try {
			List<Manager> managers = dbHelper.getManagerDao().queryForAll();
			
			if (managers.size() > 0) {
				manager = managers.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DbHelperSecured.close();
    	
    	return manager;
    }
    
    private Account mAccount;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
        setContentView(R.layout.activity_main2);

        mAccount = getIntent().getExtras().getParcelable("account");
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[] { getString(R.string.titleMap), getString(R.string.titlePsrs), getString(R.string.titleStores) }));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        updateUserInfo();
        
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype_small_beta));  

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		requestBackup();
		requestInitialSync();
	}

	private void requestUpdate() {
		Toast.makeText(getBaseContext(), getString(R.string.update_check), Toast.LENGTH_LONG).show();
		UpdateHelper.get(this).update();
	}
	
	private void requestInitialSync() {
        SharedPreferences sharedPreferences = getSharedPreferences(mAccount.name + ".global", Context.MODE_MULTI_PROCESS);
        String lastSync = sharedPreferences.getString("lastSync", "unknown");
        
        if (lastSync.equals("unknown")) {
        	requestSync(); 
        }
	}
	
	private void requestChangeUser() {
		Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(intent); 
	}
	
	private void requestSync() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        
        ContentResolver.requestSync(mAccount, AccountWrapper.ACCOUNT_AUTHORITY, settingsBundle);
	}
	
	private void requestBackup() {
		BackupManager backupManager = new BackupManager(this);
		backupManager.dataChanged();
	}
	
	private void updateUserInfo() {
        String title;
        String subTitle;
        
        Manager manager = getManager();
        
        if (manager != null) { 
        	title = manager.getName();
       		subTitle = manager.getBranch().getName();
        } else {
        	title = mAccount.name;
        	subTitle = mAccount.type;
        }
        
		getActionBar().setTitle(title); 
		getActionBar().setSubtitle(subTitle); 
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mMapFragment.updateMap();
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
        
    }
	
    private MapFragment mMapFragment;
    private PsrListFragment mPsrListFragment;
    private StoreListFragment mStoreListFragment;
    
    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    	       
        Fragment fragment = null;
        
        switch (position) {
			case 0: {
				if (mMapFragment == null) {
					mMapFragment = new MapFragment();
				}
				
				fragment = mMapFragment;
			} break;
			case 1: {
				if (mPsrListFragment == null) {
					mPsrListFragment = new PsrListFragment();
				}
				
				fragment = mPsrListFragment;
			} break;
			case 2: {
				if (mStoreListFragment == null) {
					mStoreListFragment = new StoreListFragment();
				}
				
				fragment = mStoreListFragment;
			} break;

			default: {
				return;
			}
		}
        
        Bundle arguments = new Bundle();
        arguments.putParcelable("account", mAccount); 
        
        fragment.setArguments(arguments);
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
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
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionSync: {
				requestSync();
			} break;
			case R.id.actionChangeUser: {
				requestChangeUser();
			} break;
			case R.id.checkUpdates: {
				requestUpdate();
			} break;
			case R.id.actionAbout: {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(Apps.getVersionName(this)); 
				builder.setCancelable(true);
				builder.create().show();
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
		
		private static final String TAG = "SYNCHRONIZATION";
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(mAccount.name)) { 
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started")) {
	            	Log.d(TAG, "sync:started->" + intent.getStringExtra("account"));
	            	
	            	setProgressBarIndeterminateVisibility(true); 
	            }
	            if (action.equals("ack")) {
	            	Log.d(TAG, "sync:ack->" + intent.getStringExtra("account"));
	            	
	            	setProgressBarIndeterminateVisibility(true); 
	            }
	            if (action.equals("completed")) {
	            	Log.d(TAG, "sync:completed->" + intent.getStringExtra("account")); 
	            	
	            	setProgressBarIndeterminateVisibility(false); 
	            	
	    			if (mMapFragment != null) 
	    				mMapFragment.updateMap();
	    			
	    			updateUserInfo();
	    			
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            }
	            if (action.equals("canceled")) {
	            	Log.d(TAG, "sync:canceled->" + intent.getStringExtra("account"));  
	            	
	            	setProgressBarIndeterminateVisibility(false); 
	            	
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncCanceled), Toast.LENGTH_LONG).show();	
	            }
	            if (action.equals("error")) {
	            	Log.d(TAG, "sync:error->" + intent.getStringExtra("account"));  

	            	setProgressBarIndeterminateVisibility(false); 
	            	
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
	            }
	        }
	    }
	    
	};
	
}