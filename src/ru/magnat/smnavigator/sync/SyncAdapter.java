package ru.magnat.smnavigator.sync;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.GetPsrsHelper;
import ru.magnat.smnavigator.data.GetRoutesHelper;
import ru.magnat.smnavigator.data.GetStoreStatisticsHelper;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Route;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.entities.StoreStatistics;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;

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
			
			loadStores();
			loadStoreStatistics();
			loadPsrs();
			loadRoutes();
			
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
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStores");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		final List<Store> stores = new GetStoresHelper().readJsonStream(urlConnection.getInputStream());

		urlConnection.disconnect();
		
		final Dao<Store, String> storeDao = MainDbHelper.getInstance(getContext()).getStoreDao();
		storeDao.callBatchTasks(new Callable<Void>() {
			
			public Void call() throws Exception {
				for (Store store : stores) {
					storeDao.createOrUpdate(store);
				}
				
				return null;
			}
		});
	}
	
	private void loadStoreStatistics() throws NotFoundException, IOException, SQLException {   
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStoreStatistics");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		for (StoreStatistics storeStatistics : new GetStoreStatisticsHelper().readJsonStream(urlConnection.getInputStream())) {
			MainDbHelper.getInstance(getContext()).getStoreStatisticsDao().createOrUpdate(storeStatistics);
		}

		urlConnection.disconnect();
	}
	
	private void loadPsrs() throws NotFoundException, IOException, SQLException { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getPsrs");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		for (Psr psr : new GetPsrsHelper().readJsonStream(urlConnection.getInputStream())) {
			MainDbHelper.getInstance(getContext()).getPsrDao().createOrUpdate(psr);
		}

		urlConnection.disconnect();
	}
	
	private void loadRoutes() throws NotFoundException, IOException, SQLException { 
		URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getRoutes");
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		for (Route route : new GetRoutesHelper().readJsonStream(urlConnection.getInputStream())) {
			MainDbHelper.getInstance(getContext()).getRouteDao().createOrUpdate(route);
		}

		urlConnection.disconnect();
	}

}
