package ru.magnat.smnavigator.activities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Apps;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.MainFragment;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.security.account.AccountWrapper;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.synchronization.SynchronizationListener;
import ru.magnat.smnavigator.synchronization.SynchronizationStatus;
import ru.magnat.smnavigator.update.UpdateHelper;
import ru.magnat.smnavigator.view.ManagerCardView;
import android.accounts.Account;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawer;
    private ListView mDrawerList;
    
    private Account mAccount;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

        setContentView(R.layout.main_activity);

        mAccount = getIntent().getExtras().getParcelable("account");
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DemoDrawerListener());
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_title));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[] { getString(R.string.titleMap), getString(R.string.titlePsrs), getString(R.string.titleStores) }));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        updateUserInfo();
        
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new MainFragment());
            fragmentTransaction.commit();
        }
        
		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		requestBackup();
		requestUpdate();
		requestInitialSync();
		
		progressBar = (ProgressBar) LayoutInflater.from(getBaseContext()).inflate(R.layout.progressbar, null, false);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		toolbar.setSubtitle("");
		toolbar.addView(progressBar); 
		
	    setSupportActionBar(toolbar);
	}

	private ProgressBar progressBar;
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	private class DemoDrawerListener implements DrawerLayout.DrawerListener {
		
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }

	private void requestUpdate() {
		UpdateHelper.get(this).update();
	}
	 
	private boolean mInitialSynchronize;
	
	private void requestInitialSync() {
		if (getManager() == null) {
			mInitialSynchronize = true;
			
			requestSync(); 
		} else {
			selectItem(0); 			
		}
	}
	
	private void requestChangeUser() {
		Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
		
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
	
    private Manager getManager() {
    	Manager manager = null;
    	
    	SecuredStorage dbHelper = SecuredStorage.get(this, mAccount);
		
		try {
			List<Manager> managers = dbHelper.getManagerDao().queryForAll();
			
			if (managers.size() > 0) {
				manager = managers.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
    	
    	return manager;
    }
	
	private void updateUserInfo() {
		ManagerCardView managerCardView = (ManagerCardView) findViewById(R.id.userInfo);
		managerCardView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestChangeUser();
			}
		});
		
        Manager manager = getManager();
        
        if (manager != null) { 
        	managerCardView.setManager(manager);
        	managerCardView.setVisibility(View.VISIBLE); 
        } else {
        	managerCardView.setVisibility(View.GONE); 
        }
        
        TextView version = (TextView) findViewById(R.id.version);
        version.setText(Apps.getVersionName(getBaseContext())); 
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
        mDrawerLayout.closeDrawer(mDrawer);
    	      
        Bundle arguments = new Bundle();
        arguments.putParcelable("account", mAccount);
        
        Fragment fragment = null;
        
        switch (position) {
			case 0: {
				if (mMapFragment == null) {
					mMapFragment = new MapFragment();
					
					mMapFragment.setArguments(arguments);
				}
				
				fragment = mMapFragment;
			} break;
			case 1: {
				if (mPsrListFragment == null) {
					mPsrListFragment = new PsrListFragment();
					
					mPsrListFragment.setArguments(arguments);
				}
				
				fragment = mPsrListFragment;
			} break;
			case 2: {
				if (mStoreListFragment == null) {
					mStoreListFragment = new StoreListFragment();
					
					mStoreListFragment.setArguments(arguments);
				}
				
				fragment = mStoreListFragment;
			} break;
			default: {
				return;
			}
		}
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
    
    @Override
    public void onBackPressed() {
    	if (mDrawerLayout.isDrawerOpen(mDrawer)) {
    		mDrawerLayout.closeDrawer(mDrawer); return;
    	}
    	
    	super.onBackPressed();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister receivers
		unregisterReceiver(mSyncReceiver); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu); 
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionSync: {
				requestSync();
			} break;
//			case R.id.actionChangeUser: {
//				requestChangeUser();
//			} break;
//			case R.id.checkUpdates: {
//				requestUpdate();
//			} break;
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
	
	private List<SynchronizationListener> mSyncListeners = new ArrayList<SynchronizationListener>(); 
	
	public void registerSyncListener(SynchronizationListener listener) {
		mSyncListeners.add(listener);
	}
	
	public void unregisterSyncListener(SynchronizationListener listener) {
		mSyncListeners.remove(listener);
	}
	
	private void notifySyncListeners(SynchronizationStatus status) {
		for (SynchronizationListener listener : mSyncListeners) {
			listener.onSynchronizationCompleted(status);
		}
	}
	
	private BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
		
		private static final String TAG = "SYNCHRONIZATION";
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(mAccount.name)) { 
	        	String action = intent.getStringExtra("action");
	            
	            if (action.equals("started")) {
	            	Log.d(TAG, "sync:started->" + intent.getStringExtra("account"));
	            	
	            	progressBar.setVisibility(View.VISIBLE); 
	            	
	            	notifySyncListeners(SynchronizationStatus.STARTED); 
	            }
	            if (action.equals("ack")) {
	            	Log.d(TAG, "sync:ack->" + intent.getStringExtra("account"));
	            	
	            	progressBar.setVisibility(View.VISIBLE); 
	            	
	            	notifySyncListeners(SynchronizationStatus.ACK);
	            }
	            if (action.equals("completed")) {
	            	Log.d(TAG, "sync:completed->" + intent.getStringExtra("account")); 
	            	
	            	if (mInitialSynchronize) { 
	            		selectItem(0); 
	            	}
	            	
	    			updateUserInfo();

	    			progressBar.setVisibility(View.INVISIBLE); 
	    			
	    			notifySyncListeners(SynchronizationStatus.COMPLETED);
	    			
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            }
	            if (action.equals("canceled")) {
	            	Log.d(TAG, "sync:canceled->" + intent.getStringExtra("account"));  
	            	
	            	progressBar.setVisibility(View.INVISIBLE); 
	            	
	            	notifySyncListeners(SynchronizationStatus.CANCELED);
	            	
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncCanceled), Toast.LENGTH_LONG).show();	
	            }
	            if (action.equals("error")) {
	            	Log.d(TAG, "sync:error->" + intent.getStringExtra("account"));  
	            	
	            	progressBar.setVisibility(View.INVISIBLE); 
	            	
	            	notifySyncListeners(SynchronizationStatus.ERROR);
	            	
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
	            }
	        }
	    }
	    
	};
	
}
