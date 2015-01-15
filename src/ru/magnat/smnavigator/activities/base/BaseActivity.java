package ru.magnat.smnavigator.activities.base;

import ru.magnat.smnavigator.synchronization.util.SynchronizationObserver;
import android.accounts.Account;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BaseActivity extends ActionBarActivity implements SynchronizationObserver {
	
	protected Account mAccount;

	protected ActionBarDrawerToggle mDrawerToggle;
	protected DrawerLayout mDrawerLayout;
	protected LinearLayout mDrawer;
	protected ListView mDrawerList;
	
	protected Toolbar mToolBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAccount = getIntent().getExtras().getParcelable("account");
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
