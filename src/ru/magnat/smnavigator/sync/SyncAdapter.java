package ru.magnat.smnavigator.sync;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.GetPsrsHelper;
import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.data.db.MainDbHelper;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Store;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

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
		new UpdateStoresTask().execute();
    }
    
    public class UpdateStoresTask extends AsyncTask<Void, Void, Void> {
				
    	private void loadStores() {
			try {
				URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getStores");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

				for (Store store : new GetStoresHelper().readJsonStream(urlConnection.getInputStream())) {
					MainDbHelper.getInstance(getContext()).getStoreDao().createOrUpdate(store);
				}

				urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				cancel(true);
			} catch (SQLException e) {
				e.printStackTrace();
				cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
				cancel(true);
			}
    	}
    	
    	private void loadPsrs() {
			try {
				URL url = new URL("http://" + getContext().getResources().getString(R.string.syncServer) + "/sm_getPsrs");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

				for (Psr psr : new GetPsrsHelper().readJsonStream(urlConnection.getInputStream())) {
					MainDbHelper.getInstance(getContext()).getPsrDao().createOrUpdate(psr);
				}

				urlConnection.disconnect();;
			} catch (IOException e) {
				e.printStackTrace();
				cancel(true);
			} catch (SQLException e) {
				e.printStackTrace();
				cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
				cancel(true);
			}
    	}
    	
    	@Override
    	protected void onPreExecute() {
        	Intent intent = new Intent(MainActivity.ACTION_SYNC);
        	intent.putExtra("action", "started");
        	
        	getContext().sendBroadcast(intent);
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			try {
				TimeUnit.SECONDS.sleep(3);
				
				loadStores();
				loadPsrs();
				
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
				cancel(true);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
	    	Intent intent = new Intent(MainActivity.ACTION_SYNC);
	    	intent.putExtra("action", "completed");
	    	
	    	getContext().sendBroadcast(intent);
	    	
	    	Toast.makeText(getContext(), getContext().getResources().getString(R.string.syncSuccess), Toast.LENGTH_LONG).show(); 
		}
		
		@Override
		protected void onCancelled() {
	    	Intent intent = new Intent(MainActivity.ACTION_SYNC);
	    	intent.putExtra("action", "error");
	    	
	    	getContext().sendBroadcast(intent);
	    	
	    	Toast.makeText(getContext(), getContext().getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show();
		}
		
		@Override
		protected void onCancelled(Void result) {
	    	Intent intent = new Intent(MainActivity.ACTION_SYNC);
	    	intent.putExtra("action", "error");
	    	
	    	getContext().sendBroadcast(intent);
	    	
	    	Toast.makeText(getContext(), getContext().getResources().getString(R.string.syncError), Toast.LENGTH_LONG).show(); 
		}
		
	}

}
