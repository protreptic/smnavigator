package ru.magnat.smnavigator.activities;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.fragments.MapFragment;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.update.Artifact;
import ru.magnat.smnavigator.update.CentralRepository;
import ru.magnat.smnavigator.update.DownloadArtifactActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.TaskStackBuilder;
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
    
    private Manager getManager() {
    	Manager manager = null;
    	
		MainDbHelper dbHelper = MainDbHelper.getInstance(this, mAccountHelper.getCurrentAccount());
		
		try {
			List<Manager> managers = dbHelper.getManagerDao().queryForAll();
			
			if (managers.size() > 0) {
				manager = managers.get(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		MainDbHelper.close();
    	
    	return manager;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

        setContentView(R.layout.activity_main2);

        mAccountHelper = AccountHelper.getInstance(this);
		
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        // set up the drawer's list view with items and click listener
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[] { getString(R.string.titleMap), getString(R.string.titlePsrs), getString(R.string.titleStores) }));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        String title;
        String subTitle;
        
        Manager manager = getManager();
        
        if (manager != null) { 
        	title = manager.getName();
        	subTitle = "email: " + manager.getEmail();
        } else {
        	title = mAccountHelper.getCurrentAccount().name;
        	subTitle = mAccountHelper.getCurrentAccount().type;
        }
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
		getActionBar().setTitle(title); 
		getActionBar().setSubtitle(subTitle); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype_small));  

        if (savedInstanceState == null) {
            selectItem(0);
        }
		
		// register receivers
		registerReceiver(mSyncReceiver, new IntentFilter(ACTION_SYNC)); 
		
		new AsyncTask<Void, Void, Artifact>() {

			private static final int NOTIFICATION_ID = 101; 
			
			private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			
			private CentralRepository centralRepository;
			
			protected void onPreExecute() {
				centralRepository = new CentralRepository(getBaseContext()); 
				
				Notification.Builder builder = new Builder(getBaseContext());
				builder.setSmallIcon(R.drawable.logotype_small);
				builder.setContentTitle(getString(R.string.app_name));
				builder.setContentText(getString(R.string.update_check)); 
				builder.setProgress(0, 0, true);
				builder.setAutoCancel(false);
				builder.setOngoing(true);
				
				notificationManager.notify(NOTIFICATION_ID, builder.build()); 
			};
			
			@Override
			protected Artifact doInBackground(Void... params) {
				return centralRepository.update();
			}
			
			protected void onPostExecute(Artifact artifact) {
				notificationManager.cancel(NOTIFICATION_ID);
				
				if (artifact != null) {
					Intent intent = new Intent(getBaseContext(), DownloadArtifactActivity.class);
					intent.putExtra("artifact", artifact);
					
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
					stackBuilder.addParentStack(DownloadArtifactActivity.class);
					stackBuilder.addNextIntent(intent);
					
					PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					
					long[] pattern = {3, 1000, 1000};
					
					Notification.Builder builder = new Builder(getBaseContext());
					builder.setSmallIcon(R.drawable.logotype_small);
					builder.setContentTitle(getString(R.string.app_name));
					builder.setContentText(String.format(getString(R.string.update_update_available), artifact.getVersionName())); 
					builder.setVibrate(pattern);					
					builder.setContentIntent(pendingIntent);
					
					notificationManager.notify(NOTIFICATION_ID, builder.build()); 
				}
			};
			
		}.execute(); 
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
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    	       
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
				
				final Account account = accountHelper.getCurrentAccount();
						        
				AccountManager accountManager = AccountManager.get(getBaseContext());
				accountManager.invalidateAuthToken(AccountWrapper.ACCOUNT_TYPE, null); 
				accountManager.getAuthToken(account, AccountWrapper.ACCOUNT_TYPE, null, getParent(), new AccountManagerCallback<Bundle>() {
					
					@Override
					public void run(AccountManagerFuture<Bundle> future) {
						try {
							Bundle bundle = future.getResult();
							
							// Pass the settings flags by inserting them in a bundle
					        Bundle settingsBundle = new Bundle();
					        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
					        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
					        
					        settingsBundle.putString(AccountManager.KEY_AUTHTOKEN, bundle.getString(AccountManager.KEY_AUTHTOKEN)); 
					        
					        // Request the sync for the default account, authority, and
					        // manual sync settings
					        ContentResolver.requestSync(account, AccountWrapper.ACCOUNT_AUTHORITY, settingsBundle);
						} catch (OperationCanceledException e) {
							e.printStackTrace();
						} catch (AuthenticatorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}, null);
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
	    			
	    			if (mMapFragment != null) 
	    				mMapFragment.updateMap();
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