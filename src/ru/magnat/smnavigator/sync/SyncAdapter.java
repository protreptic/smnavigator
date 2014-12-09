package ru.magnat.smnavigator.sync;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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
import ru.magnat.smnavigator.model.StoreStatistics;
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
    
    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	sendStarted();
    	
		try {
			TimeUnit.SECONDS.sleep(3);
		
			mMainDbHelper = MainDbHelper.getInstance(getContext());
			
			loadStores();
			loadStoreStatistics();
			loadPsrs();
			loadRoutes();
			
			MainDbHelper.close();
			
			TimeUnit.SECONDS.sleep(3);
		} catch (SQLException e) {
			e.printStackTrace();
			sendError();
		} catch (InterruptedException e) {
			e.printStackTrace();
			sendError();
		} catch (NotFoundException e) {
			e.printStackTrace();
			sendError();
		} catch (IOException e) {
			e.printStackTrace();
			sendError();
		} catch (Exception e) {
			e.printStackTrace();
			sendError();	
	    }
		
		sendCompleted();
    }
    
    private void sendStarted() {
    	Intent intentStarted = new Intent(MainActivity.ACTION_SYNC);
    	intentStarted.putExtra("action", "started");
    	
    	getContext().sendBroadcast(intentStarted);
    }
    
    private void sendCompleted() {
    	Intent intentCompleted = new Intent(MainActivity.ACTION_SYNC);
    	intentCompleted.putExtra("action", "completed");
    	
    	getContext().sendBroadcast(intentCompleted);
    }
    
    private void sendError() {
    	Intent intentError = new Intent(MainActivity.ACTION_SYNC);
    	intentError.putExtra("action", "error");
    	
    	getContext().sendBroadcast(intentError);
    }
    
	private void loadStores() throws Exception { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getAllStores");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<Store> stores = new GetStoresHelper().readJsonStream(urlConnection.getInputStream());

		urlConnection.disconnect();
		
		Dao<Store, String> storeDao = mMainDbHelper.getStoreDao();
		storeDao.setObjectCache(false); 
		
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);

			//Log.d("SQL LOG", "id = " + store.getId() + " name = " + store.getName() + " address = " + store.getAddress() + " status = " + status);
		}
	}
	
	private void loadStoreStatistics() throws NotFoundException, IOException, SQLException {   
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStoreStatistics");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		List<StoreStatistics> storeStatistics = new GetStoreStatisticsHelper().readJsonStream(urlConnection.getInputStream());
		
		urlConnection.disconnect();
		
		Dao<StoreStatistics, String> storeStatisticDao = mMainDbHelper.getStoreStatisticsDao();
		storeStatisticDao.setObjectCache(false); 
		
		storeStatisticDao.delete(storeStatisticDao.queryForAll());
		
		for (StoreStatistics storeStatistic : storeStatistics) {
			storeStatisticDao.createOrUpdate(storeStatistic);
			
			//Log.d("SQL LOG", "id = " + storeStatistic.getId() + " status = " + status);
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
			
			//Log.d("SQL LOG", "id = " + psr.getId() + " status = " + status);
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
			
			//Log.d("SQL LOG", "id = " + psr.getId() + " status = " + status);
		}
	}

}
