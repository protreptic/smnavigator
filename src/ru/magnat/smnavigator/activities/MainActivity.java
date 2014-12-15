package ru.magnat.smnavigator.activities;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.update.CentralRepository;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private MenuItem mSyncItem;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private AccountHelper mAccountHelper;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

        setContentView(R.layout.activity_main2);

        mAccountHelper = AccountHelper.getInstance(this);
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[] { getString(R.string.titleMap), getString(R.string.titlePsrs), getString(R.string.titleStores) }));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
		getActionBar().setTitle(mAccountHelper.getCurrentAccount().name); 
		getActionBar().setSubtitle(mAccountHelper.getCurrentAccount().type); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_action_view_as_grid)); 

        if (savedInstanceState == null) {
            selectItem(0);
        }
		
		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		AccountManager accountManager = AccountManager.get(getBaseContext());
		accountManager.invalidateAuthToken(AccountWrapper.ACCOUNT_TYPE, null); 
		accountManager.getAuthToken(mAccountHelper.getCurrentAccount(), AccountWrapper.ACCOUNT_TYPE, null, getParent(), new AccountManagerCallback<Bundle>() {
			 
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					
					final String sessionToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					final String packageName = getPackageName();
					
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							CentralRepository centralRepository = new CentralRepository(getBaseContext(), sessionToken, packageName); 
							
							centralRepository.update();
							
							return null;
						}
					}.execute(); 
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, null);
		

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
        // update the main content by replacing fragments
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
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
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
		
		private static final String TAG = "SYNCHRONIZATION";
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(AccountHelper.getInstance(getBaseContext()).getCurrentAccount().name)) { 
	        	Animation mRotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate360);
	        	
	            String action = intent.getStringExtra("action");
	            
	            if (action.equals("started") && mSyncItem != null) {
	            	Log.d(TAG, "sync:started->" + intent.getStringExtra("account"));
	            	
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
	            	Log.d(TAG, "sync:ask->" + intent.getStringExtra("account"));
	            	
	    			LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.animated_refresh_icon, new LinearLayout(getBaseContext()), false); 

	    		    view.startAnimation(mRotate);
	    		    view.setLayoutParams(new LayoutParams(54, 54)); 
	    			
	    		    if (mSyncItem.getActionView() == null) {
	    		    	mSyncItem.setActionView(view);
	    		    }
	            }
	            if (action.equals("completed") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d(TAG, "sync:completed->" + intent.getStringExtra("account")); 
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			
	    			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    			
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_ok));
	    			mSyncItem.setTitle(getResources().getString(R.string.syncLastSuccessAttempt) + " " + dateFormat.format(new Date(System.currentTimeMillis()))); 
	            }
	            if (action.equals("canceled") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d(TAG, "sync:canceled->" + intent.getStringExtra("account"));  
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncCanceled), Toast.LENGTH_LONG).show();	
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	            }
	            if (action.equals("error") && mSyncItem != null && mSyncItem.getActionView() != null) {
	            	Log.d(TAG, "sync:error->" + intent.getStringExtra("account"));  
	            	Toast.makeText(getBaseContext(), getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();	
	            	
	    			mSyncItem.getActionView().clearAnimation();
	    			mSyncItem.setActionView(null);
	    			 
	    			mSyncItem.setIcon(getResources().getDrawable(R.drawable.ic_action_refresh_error));
	            }
	        }
	    }
	    
	};
	
}