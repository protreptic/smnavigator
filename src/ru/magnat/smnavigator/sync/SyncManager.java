package ru.magnat.smnavigator.sync;

import ru.magnat.smnavigator.synchronization.util.SyncObservable;
import ru.magnat.smnavigator.synchronization.util.SyncObserver;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncManager extends BroadcastReceiver {

	private static final String TAG = "SYNCHRONIZATION";
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private SyncObservable mSynchronizationObservable = new SyncObservable();
	
	public void registerSyncObserver(SyncObserver observer) {
		mSynchronizationObservable.registerObserver(observer);
	}
	
	public void unregisterSyncObserver(SyncObserver observer) {
		mSynchronizationObservable.unregisterObserver(observer);
	}
	
	public void unregisterAllObservers() {
		mSynchronizationObservable.unregisterAll();
	}
	
	private Account mAccount;
	
	public SyncManager(Account account) {
		mAccount = account;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(mAccount.name)) { 
        	String action = intent.getStringExtra("action");
            
            if (action.equals("started")) {
            	Log.d(TAG, "sync:started->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyStatusChanged(SyncStatus.STARTED);
            }
            if (action.equals("ack")) {
            	Log.d(TAG, "sync:ack->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyStatusChanged(SyncStatus.ACK);
            }
            if (action.equals("completed")) {
            	Log.d(TAG, "sync:completed->" + intent.getStringExtra("account")); 
            	
            	mSynchronizationObservable.notifyStatusChanged(SyncStatus.COMPLETED);
            }
            if (action.equals("canceled")) {
            	Log.d(TAG, "sync:canceled->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyStatusChanged(SyncStatus.CANCELED);
            }
            if (action.equals("error")) {
            	Log.d(TAG, "sync:error->" + intent.getStringExtra("account"));  
            	
            	mSynchronizationObservable.notifyStatusChanged(SyncStatus.ERROR);
            }
        }
	}
	
}
