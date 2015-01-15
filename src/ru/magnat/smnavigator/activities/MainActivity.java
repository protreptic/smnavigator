package ru.magnat.smnavigator.activities;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.account.AccountSettings;
import ru.magnat.smnavigator.activities.base.BaseActivity;
import ru.magnat.smnavigator.fragments.CustomerListFragment;
import ru.magnat.smnavigator.fragments.EmptyFragment;
import ru.magnat.smnavigator.fragments.InitFragment;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.sync.SyncStatus;
import ru.magnat.smnavigator.update.UpdateHelper;
import ru.magnat.smnavigator.view.ManagerCardView;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	public static class MenuItemViewHolder {

		public ImageView image1;
		public TextView textView;
		
	}
	
    private class MenuItemAdapter extends BaseAdapter {

		private Typeface mRobotoCondensedBold;
		
		public MenuItemAdapter() {
			mRobotoCondensedBold = Fonts.get(getBaseContext()).getTypeface("RobotoCondensed-Bold");
		}
    	
		@Override
		public int getCount() {
			return mMenuItems.length;
		}
		
		@Override
		public Object getItem(int position) {
			return mMenuItems[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuItemViewHolder holder;
			
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.drawer_list_item, parent, false);
				
				holder = new MenuItemViewHolder();
				holder.image1 = (ImageView) convertView.findViewById(R.id.image1);
				
				holder.textView = (TextView) convertView.findViewById(R.id.text1);
				holder.textView.setTypeface(mRobotoCondensedBold);
				
				convertView.setTag(holder); 
			} else {
				holder = (MenuItemViewHolder) convertView.getTag();
			}
			
			holder.textView.setText(getString((Integer) getItem(position))); 
			holder.image1.setImageDrawable(getResources().getDrawable(mMenuIcons[position])); 
			
			return convertView;
		}
    	
    }
    
    private int[] mMenuIcons = new int[] { R.drawable.map_map_marker, R.drawable.user_male , R.drawable.handshake, R.drawable.shop };
    private int[] mMenuItems = new int[] { R.string.titleMap, R.string.titlePsrs, R.string.titleCustomers, R.string.titleStores };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

        setContentView(R.layout.main_activity);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DefaultDrawerListener());
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_title));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new MenuItemAdapter()); 
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        initToolbar();
        try {
			mManager = new ReadManager().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        
        if (savedInstanceState != null) {
        	fragmentPosition = savedInstanceState.getInt("fragmentPosition", -1);
        }
        
		//requestUpdate();
		requestInitialSync();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	    mMapFragment = null;
	    mPsrListFragment = null;
	    mStoreListFragment = null;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		
		bundle.putInt("fragmentPosition", fragmentPosition);
 	}
	
	private void initToolbar() {
		progressBar = (ProgressBar) LayoutInflater.from(getBaseContext()).inflate(R.layout.progressbar, null, false);
		progressBar.setVisibility(View.INVISIBLE); 
		
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		mToolBar.setTitle("");
		mToolBar.setSubtitle("");
		mToolBar.addView(progressBar); 
		
	    setSupportActionBar(mToolBar);
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
    
	@SuppressWarnings("unused")
	private void requestUpdate() {
		UpdateHelper.get(this).update();
	}
	 
	private boolean mInitialSynchronize;
	
	private void requestInitialSync() {
		if (mManager == null) {
			mInitialSynchronize = true;
			
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new InitFragment());
            fragmentTransaction.commit();	
			
			requestSync(); 
		} else {
			if (fragmentPosition == -1) {
	            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	            fragmentTransaction.replace(R.id.content_frame, new EmptyFragment());
	            fragmentTransaction.commit();	
	            
	            mDrawerLayout.openDrawer(mDrawer);
			} else {
				selectItem(fragmentPosition); 
			}
		}
	}
	
	private void requestChangeUser() {
		Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
		
		startActivity(intent); 
	}
	
	private Manager mManager;
	
	private void requestSync() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putString("certificatePath", "server-certificate.pem"); 
        
        ContentResolver.requestSync(mAccount, AccountSettings.ACCOUNT_AUTHORITY, settingsBundle);
	}
	
    private class ReadManager extends AsyncTask<Void, Void, Manager> {

    	private ManagerCardView managerView;
    	
    	@Override
    	protected void onPreExecute() {
    		managerView = (ManagerCardView) findViewById(R.id.userInfo);
    		managerView.setVisibility(View.GONE); 
    		managerView.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				requestChangeUser();
    			}
    		});
    	}
    	
		@Override
		protected Manager doInBackground(Void... params) {
			Manager manager = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getBaseContext(), mAccount);
			
			try {
				List<Manager> managers = securedStorage.getManagerDao().queryForAll();
				
				if (managers.size() > 0) {
					manager = managers.get(0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return manager;
		}
		
		@Override
		protected void onPostExecute(Manager manager) {
			if (manager != null) { 
				managerView.setManager(manager);
				managerView.setVisibility(View.VISIBLE); 
				
				mManager = manager; 
	        }
		}
    	
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
        
    }
	
    private MapFragment mMapFragment;
    private PsrListFragment mPsrListFragment;
    private CustomerListFragment mCustomerListFragment;
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
					
					if (mManager != null) {
						arguments.putBoolean("initialGeopoint", true); 
						arguments.putDouble("latitude", mManager.getBranch().getLocation().getLatitude());
						arguments.putDouble("longitude", mManager.getBranch().getLocation().getLongitude());
					}
					
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
				if (mCustomerListFragment == null) {
					mCustomerListFragment = new CustomerListFragment();
					
					mCustomerListFragment.setArguments(arguments);
				}
				
				fragment = mCustomerListFragment;
			} break;
			case 3: {
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
        
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
        	getSupportFragmentManager().popBackStackImmediate();
        }
        
        fragmentPosition = position;
        
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
    
    private int fragmentPosition = -1;
    
    @Override
    public void onBackPressed() {
    	if (mDrawerLayout.isDrawerOpen(mDrawer)) {
    		mDrawerLayout.closeDrawer(mDrawer); return;
    	}
    	
    	super.onBackPressed();
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
	
	@Override
	public void onStatusChanged(SyncStatus status) {
		switch (status) {
			case STARTED: {
		    	progressBar.setVisibility(View.VISIBLE); 
		    	
		    	if (mInitialSynchronize) { 
		    		mToolBar.setVisibility(View.GONE);
		    	}
			} break;
			case ACK: {
		    	progressBar.setVisibility(View.VISIBLE); 
		    	
		    	if (mInitialSynchronize) { 
		    		mToolBar.setVisibility(View.GONE);
		    	}
			} break;
			case COMPLETED: {
		    	if (mInitialSynchronize) { 
		    		mToolBar.setVisibility(View.VISIBLE);
		    		selectItem(0); 
		    	}
		    	
		    	new ReadManager().execute();
		    	
				progressBar.setVisibility(View.INVISIBLE); 
				
		    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
			} break;
			case CANCELED: {
		    	progressBar.setVisibility(View.INVISIBLE); 
		    	
		    	if (mInitialSynchronize) { 
		    		mToolBar.setVisibility(View.VISIBLE);
		    	}
		    	
		    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncCanceled), Toast.LENGTH_LONG).show();	
			} break;
			case ERROR: {
		    	progressBar.setVisibility(View.INVISIBLE); 
		    	
		    	if (mInitialSynchronize) { 
		    		mToolBar.setVisibility(View.VISIBLE);
		    	}
		    	
		    	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
			} break;
			default: {
				throw new RuntimeException();
			}
		}
	}
	
}
