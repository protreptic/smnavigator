package ru.magnat.smnavigator.activities.base;

import ru.magnat.smnavigator.sync.SyncManager;
import ru.magnat.smnavigator.sync.util.SyncObserver;
import android.accounts.Account;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public abstract class BaseActivity extends ActionBarActivity implements SyncObserver {
	
	protected Account mAccount;
	protected SyncManager mSynchronizationManager;
	
	protected ActionBarDrawerToggle mDrawerToggle;
	protected DrawerLayout mDrawerLayout;
	protected LinearLayout mDrawer;
	protected ListView mDrawerList;
	
	protected Toolbar mToolBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAccount = getIntent().getExtras().getParcelable("account");
		mSynchronizationManager = new SyncManager(mAccount);
		
		// register receivers
		registerReceiver(mSynchronizationManager, new IntentFilter(SyncManager.ACTION_SYNC)); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// unregister receivers
		unregisterReceiver(mSynchronizationManager); 
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mSynchronizationManager.registerSyncObserver(this); 
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		mSynchronizationManager.unregisterSyncObserver(this); 
	}
	
    public void registerSyncObserver(SyncObserver observer) {
    	mSynchronizationManager.registerSyncObserver(observer); 
    }
    
    public void unregisterSyncObserver(SyncObserver observer) {
    	mSynchronizationManager.unregisterSyncObserver(observer); 
    }
	
	public class DefaultDrawerListener implements DrawerLayout.DrawerListener {
		
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
	
}
