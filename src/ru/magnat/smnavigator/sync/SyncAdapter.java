package ru.magnat.smnavigator.sync;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.GetPsrsHelper;
import ru.magnat.smnavigator.data.GetRoutesHelper;
import ru.magnat.smnavigator.data.GetStoreStatisticsHelper;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Measure;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;

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
    
    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;    	
    	
    	sendNotification("started", account.name);
    	
    	Timer timer = new Timer("askSender");
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ask", account.name);
			}
		}, 0, 1000);
    	
		try {
			TimeUnit.SECONDS.sleep(3);
		
			mMainDbHelper = MainDbHelper.getInstance(getContext(), account);
			
			loadStores();
			loadStoreStatistics();
			loadPsrs();
			loadRoutes();
			
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
    
	private void loadStores() throws Exception { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStores");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Store> stores = new GetStoresHelper().readJsonStream(urlConnection.getInputStream());

		urlConnection.disconnect();
		
		Dao<Store, String> storeDao = mMainDbHelper.getStoreDao();
		storeDao.setObjectCache(false); 
		
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);
		}
	}
	
	private void loadStoreStatistics() throws NotFoundException, IOException, SQLException {   
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStoreStatistics");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Measure> measures = new GetStoreStatisticsHelper().readJsonStream(urlConnection.getInputStream());
		
		urlConnection.disconnect();
		
		Dao<Measure, String> measureDao = mMainDbHelper.getMeasureDao();
		measureDao.setObjectCache(false); 
		
		measureDao.delete(measureDao.queryForAll());
		
		for (Measure measure : measures) {
			measureDao.createOrUpdate(measure);
		}
	}
	
	private void loadPsrs() throws NotFoundException, IOException, SQLException { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getPsrs");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Psr> psrs = new GetPsrsHelper().readJsonStream(urlConnection.getInputStream());
		
		urlConnection.disconnect();
		
		Dao<Psr, String> psrDao = mMainDbHelper.getPsrDao();
		psrDao.setObjectCache(false); 
		
		psrDao.delete(psrDao.queryForAll());
		
		for (Psr psr : psrs) {
			psrDao.createOrUpdate(psr);
		}
	}
	
	private void loadRoutes() throws NotFoundException, IOException, SQLException { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getRoutes");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Route> routes = new GetRoutesHelper().readJsonStream(urlConnection.getInputStream());
		
		urlConnection.disconnect();
		
		Dao<Route, String> routeDao = mMainDbHelper.getRouteDao();
		routeDao.setObjectCache(false); 
		
		routeDao.delete(routeDao.queryForAll());
		
		for (Route route : routes) {
			routeDao.createOrUpdate(route);
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
