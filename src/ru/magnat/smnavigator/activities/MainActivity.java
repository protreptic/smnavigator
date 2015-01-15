package ru.magnat.smnavigator.activities;

import java.sql.SQLException;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Apps;
import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.MainFragment;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.fragments.experimental.TrackFragment;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.security.account.AccountWrapper;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.synchronization.SynchronizationManager;
import ru.magnat.smnavigator.synchronization.util.SynchronizationObserver;
import ru.magnat.smnavigator.update.UpdateHelper;
import ru.magnat.smnavigator.view.ManagerCardView;
import android.accounts.Account;
import android.app.backup.BackupManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements SynchronizationObserver {
	
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawer;
    private ListView mDrawerList;
    
    private Account mAccount;
    
    private class MenuAdapter extends ArrayAdapter<String> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
			
			TextView view = (TextView) layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
			view.setTypeface(Fonts.get(getBaseContext()).getTypeface("RobotoCondensed-Bold"));  
			view.setText(getItem(position)); 
			 
			return view;
		}

		public MenuAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
		}
    	
    }
    
    private String[] menus;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		menus = new String[] {
	    		getString(R.string.titleMap), 
	    		getString(R.string.titlePsrs), 
	    		getString(R.string.titleStores),
	    		getString(R.string.titleTrack)
	    	};
		
        setContentView(R.layout.main_activity);

        mAccount = getIntent().getExtras().getParcelable("account");
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DemoDrawerListener());
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_title));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new MenuAdapter(getBaseContext(), 0, menus)); 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        updateUserInfo();
        
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new MainFragment());
            fragmentTransaction.commit();
        }
        
        mSynchronizationManager = new SynchronizationManager(mAccount);
        
		// register receivers
		registerReceiver(mSynchronizationManager, new IntentFilter(SynchronizationManager.ACTION_SYNC)); 
		
		requestBackup();
		requestUpdate();
		requestInitialSync();
		
		progressBar = (ProgressBar) LayoutInflater.from(getBaseContext()).inflate(R.layout.progressbar, null, false);
		
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		mToolBar.setTitle("");
		mToolBar.setSubtitle("");
		mToolBar.addView(progressBar); 
		
	    setSupportActionBar(mToolBar);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mSynchronizationManager.registerSynchronizationObserver(this); 
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		mSynchronizationManager.unregisterSynchronizationObserver(this); 
	}
	
	private Toolbar mToolBar;
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
    private StoreFragment mTrackFragment;
    
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
			case 3: {
				if (mTrackFragment == null) {
					mTrackFragment = new StoreFragment();
					
					mTrackFragment.setArguments(arguments);
				}
				
				fragment = mTrackFragment;
			} break;
			default: {
				return;
			}
		}
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
    
    public void registerSynchronizationObserver(SynchronizationObserver observer) {
    	mSynchronizationManager.registerSynchronizationObserver(observer); 
    }
    
    public void unregisterSynchronizationObserver(SynchronizationObserver observer) {
    	mSynchronizationManager.unregisterSynchronizationObserver(observer); 
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
		unregisterReceiver(mSynchronizationManager); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu); 
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				mDrawerLayout.openDrawer(mDrawer);
			} break;
			case R.id.actionSync: {
				requestSync();
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private SynchronizationManager mSynchronizationManager;
	
	@Override
	public void onStarted() {
    	progressBar.setVisibility(View.VISIBLE); 
    	
    	if (mInitialSynchronize) { 
    		mToolBar.setVisibility(View.GONE);
    	}
	}

	@Override
	public void onAck() {
    	progressBar.setVisibility(View.VISIBLE); 
    	
    	if (mInitialSynchronize) { 
    		mToolBar.setVisibility(View.GONE);
    	}
	}

	@Override
	public void onCompleted() {
    	if (mInitialSynchronize) { 
    		mToolBar.setVisibility(View.VISIBLE);
    		selectItem(0); 
    	}
    	
		updateUserInfo();

		progressBar.setVisibility(View.INVISIBLE); 
		
    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 

	}

	@Override
	public void onCanceled() {
    	progressBar.setVisibility(View.INVISIBLE); 
    	
    	if (mInitialSynchronize) { 
    		mToolBar.setVisibility(View.VISIBLE);
    	}
    	
    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncCanceled), Toast.LENGTH_LONG).show();	
	}

	@Override
	public void onError() {
    	progressBar.setVisibility(View.INVISIBLE); 
    	
    	if (mInitialSynchronize) { 
    		mToolBar.setVisibility(View.VISIBLE);
    	}
    	
    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	

	}
	
}
