package ru.magnat.smnavigator.sync;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.j256.ormlite.dao.Dao;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }
    
    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }
    
    private MainDbHelper mMainDbHelper;
    private Account mAccount;
    
    private String mAuthToken;
    
    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;    	
    	
    	sendNotification("started", account.name);
    	
    	mAuthToken = extras.getString(AccountManager.KEY_AUTHTOKEN);
    	
    	Timer timer = new Timer("askSender");
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ask", account.name);
			}
		}, 0, 1500);
    	
		try {
			TimeUnit.SECONDS.sleep(3);
		
			mMainDbHelper = MainDbHelper.getInstance(getContext(), account);
			
			getManager();
			getBranch();
			getDepartment();
			getPsr();
			getRoute();
			getStore();
			getMeasure();
			
			MainDbHelper.close();
			
			TimeUnit.SECONDS.sleep(3);
		} catch (Exception e) {
			e.printStackTrace();
			sendNotification("error", account.name);	
	    }
		
		timer.cancel();
		
		sendNotification("completed", account.name);
    }
    
    private void sendNotification(String action, String account) {
    	Intent intentStarted = new Intent(MainActivity.ACTION_SYNC);
    	intentStarted.putExtra("action", action);
    	intentStarted.putExtra("account", account);
    	
    	getContext().sendBroadcast(intentStarted);
    }
    
    private void getManager() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getManager?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		List<Manager> managers = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Manager>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Manager, String> managerDao = mMainDbHelper.getManagerDao();
		managerDao.setObjectCache(false); 
		
		managerDao.delete(managerDao.queryForAll());
		
		for (Manager manager : managers) {
			managerDao.createOrUpdate(manager);
			
			Log.d("", manager.toString()); 
		}
    }
    
    private void getBranch() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getBranch?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		List<Branch> branches = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Branch>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Branch, String> branchDao = mMainDbHelper.getBranchDao();
		branchDao.setObjectCache(false); 
		
		branchDao.delete(branchDao.queryForAll());
		
		for (Branch branch : branches) {
			branchDao.createOrUpdate(branch);
			
			Log.d("", branch.toString()); 
		}
    }
    
    private void getDepartment() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getDepartment?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		List<Department> departments = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Department>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Department, String> departmentDao = mMainDbHelper.getDepartmentDao();
		departmentDao.setObjectCache(false); 
		
		departmentDao.delete(departmentDao.queryForAll());
		
		for (Department department : departments) {
			departmentDao.createOrUpdate(department);
			
			Log.d("", department.toString()); 
		}
    }
    
    private void getPsr() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getPsr?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		List<Psr> psrs = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Psr>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Psr, String> psrDao = mMainDbHelper.getPsrDao();
		psrDao.setObjectCache(false); 
		
		psrDao.delete(psrDao.queryForAll());
		
		for (Psr psr : psrs) {
			psrDao.createOrUpdate(psr);
			
			Log.d("", psr.toString()); 
		}
    }
    
    private void getRoute() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getRoute?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		List<Route> routes = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Route>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Route, String> routeDao = mMainDbHelper.getRouteDao();
		routeDao.setObjectCache(false); 
		
		routeDao.delete(routeDao.queryForAll());
		
		for (Route route : routes) {
			routeDao.createOrUpdate(route);
			
			Log.d("", route.toString()); 
		}
    }
    
	private void getStore() throws Exception { 
		URL url = new URL(getContext().getResources().getString(R.string.syncServer) + "/sm_getStore?token=" + mAuthToken);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Store> stores = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Store>>() {}.getType());

		urlConnection.disconnect();
		
		Dao<Store, String> storeDao = mMainDbHelper.getStoreDao();
		storeDao.setObjectCache(false); 
		
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);
			
			Log.d("", store.toString()); 
		}
	}
	
	private void getMeasure() throws Exception {   
		URL url = new URL(getContext().getResources().getString(R.string.syncServer) + "/sm_getMeasure?token=" + mAuthToken);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Measure> measures = new Gson().fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Measure>>() {}.getType());
		
		urlConnection.disconnect();
		
		Dao<Measure, String> measureDao = mMainDbHelper.getMeasureDao();
		measureDao.setObjectCache(false); 
		
		measureDao.delete(measureDao.queryForAll());
		
		for (Measure measure : measures) {
			measureDao.createOrUpdate(measure);
			
			Log.d("", measure.toString()); 
		}
	}

	@Override
	public void onSyncCanceled() {
		super.onSyncCanceled();
		
		sendNotification("canceled", mAccount.name); 
	}

	@Override
	public void onSyncCanceled(Thread thread) {
		super.onSyncCanceled(thread);
		
		sendNotification("canceled", mAccount.name); 
	}

}
