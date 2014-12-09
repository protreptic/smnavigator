package ru.magnat.smnavigator;

import org.acra.annotation.ReportsCrashes;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.util.Device;
import ru.magnat.smnavigator.util.Network;

@ReportsCrashes(
		formKey = "", // This is required for backward compatibility but not used
		formUri = "http://mob1.magnat.ru:8081/ws_acra_submit_crash_report",
		mode = ReportingInteractionMode.TOAST,
	    resToastText = R.string.crash_message,
		socketTimeout = 5000
	)
public class Application extends android.app.Application {
	
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 45L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
	
	// Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "ru.magnat.smnavigator.auth";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "ru.magnat.smnavigator";
    // The account name
    public static final String ACCOUNT = "syncAccount";
    // Instance fields
    private static Account sAccount;
	
	@Override
	public void onCreate() {
		super.onCreate(); initAcra();
		
		MainDbHelper.getInstance(getBaseContext());
		
		sAccount = addSyncAccount(this);
		
	    // turn on periodic sync
	    //ContentResolver.addPeriodicSync(sAccount, Application.AUTHORITY, new Bundle(), SYNC_INTERVAL);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		// turn off periodic sync
		//ContentResolver.removePeriodicSync(sAccount, Application.AUTHORITY, new Bundle());
	}
	
	private void initAcra() {
        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData("device_id", Device.getDeviceId(this));
        ACRA.getErrorReporter().putCustomData("mac_wlan0", Network.getMACAddress("wlan0"));
        ACRA.getErrorReporter().putCustomData("mac_eth0", Network.getMACAddress("eth0"));
        ACRA.getErrorReporter().putCustomData("ip_v4", Network.getIPAddress(true));
        ACRA.getErrorReporter().putCustomData("ip_v6", Network.getIPAddress(false));
	}
	
	public static void sync() {
		// Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        
        // Request the sync for the default account, authority, and
        // manual sync settings
        ContentResolver.requestSync(sAccount, Application.AUTHORITY, settingsBundle);
	}
	
	public static Account addSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        
        return newAccount;
    }
	
}
