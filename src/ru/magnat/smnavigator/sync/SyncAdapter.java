package ru.magnat.smnavigator.sync;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import ru.magnat.smnavigator.data.GetStoresHelper;
import ru.magnat.smnavigator.data.db.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    
	private Context mContext;
	
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        
        mContext = context;
        
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
    	Log.d("", "onPerformSync: started!");
    	
		new UpdateStoresTask().execute();
    	
    	Log.d("", "onPerformSync: completed!");
    }
    
    public class UpdateStoresTask extends AsyncTask<Void, Void, Void> {
				
		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL("http://sfs.magnat.ru:8081/sm_get_outlets");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
				for (Store store : new GetStoresHelper().readJsonStream(urlConnection.getInputStream())) {
					MainDbHelper.getInstance(mContext).getStoreDao().createOrUpdate(store);
				}

				urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
	}

}
