package ru.magnat.smnavigator.synchronization;

import ru.magnat.smnavigator.synchronization.util.SynchronizationObservable;
import ru.magnat.smnavigator.synchronization.util.SynchronizationObserver;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SynchronizationManager extends BroadcastReceiver {

	private static final String TAG = "SYNCHRONIZATION";
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC"; 
	
	private SynchronizationObservable mSynchronizationObservable = new SynchronizationObservable();
	
	public void registerSynchronizationObserver(SynchronizationObserver observer) {
		mSynchronizationObservable.registerObserver(observer);
	}
	
	public void unregisterSynchronizationObserver(SynchronizationObserver observer) {
		mSynchronizationObservable.unregisterObserver(observer);
	}
	
	public void unregisterAllObservers() {
		mSynchronizationObservable.unregisterAll();
	}
	
	private Account mAccount;
	
	public SynchronizationManager(Account account) {
		mAccount = account;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_SYNC) && intent.getStringExtra("account").equals(mAccount.name)) { 
        	String action = intent.getStringExtra("action");
            
            if (action.equals("started")) {
            	Log.d(TAG, "sync:started->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyStarted();
            }
            if (action.equals("ack")) {
            	Log.d(TAG, "sync:ack->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyAck();
            }
            if (action.equals("completed")) {
            	Log.d(TAG, "sync:completed->" + intent.getStringExtra("account")); 
            	
            	mSynchronizationObservable.notifyCompleted();
            }
            if (action.equals("canceled")) {
            	Log.d(TAG, "sync:canceled->" + intent.getStringExtra("account"));
            	
            	mSynchronizationObservable.notifyCanceled();
            }
            if (action.equals("error")) {
            	Log.d(TAG, "sync:error->" + intent.getStringExtra("account"));  
            	
            	mSynchronizationObservable.notifyError();
            }
        }
	}
	
}
