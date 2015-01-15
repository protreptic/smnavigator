package ru.magnat.smnavigator.activities.base;

import ru.magnat.smnavigator.synchronization.SynchronizationManager;
import ru.magnat.smnavigator.synchronization.util.SynchronizationObserver;
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

public abstract class BaseActivity extends ActionBarActivity implements SynchronizationObserver {
	
	protected Account mAccount;
	protected SynchronizationManager mSynchronizationManager;
	
	protected ActionBarDrawerToggle mDrawerToggle;
	protected DrawerLayout mDrawerLayout;
	protected LinearLayout mDrawer;
	protected ListView mDrawerList;
	
	protected Toolbar mToolBar;
	
	protected String[] mMenuItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAccount = getIntent().getExtras().getParcelable("account");
		mSynchronizationManager = new SynchronizationManager(mAccount);
		
		// register receivers
		registerReceiver(mSynchronizationManager, new IntentFilter(SynchronizationManager.ACTION_SYNC)); 
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
		
		mSynchronizationManager.registerSynchronizationObserver(this); 
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		mSynchronizationManager.unregisterSynchronizationObserver(this); 
	}
	
    public void registerSynchronizationObserver(SynchronizationObserver observer) {
    	mSynchronizationManager.registerSynchronizationObserver(observer); 
    }
    
    public void unregisterSynchronizationObserver(SynchronizationObserver observer) {
    	mSynchronizationManager.unregisterSynchronizationObserver(observer); 
    }
	
	@Override
	public void onStarted() {
		
	}

	@Override
	public void onAck() {
		
	}

	@Override
	public void onCompleted() {
		
	}

	@Override
	public void onCanceled() {
		
	}

	@Override
	public void onError() {
		
	}
	 
	public class DemoDrawerListener implements DrawerLayout.DrawerListener {
		
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
