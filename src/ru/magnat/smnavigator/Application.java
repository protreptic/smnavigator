package ru.magnat.smnavigator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import ru.magnat.smnavigator.data.db.MainDbHelper;

public class Application extends android.app.Application {
	
	// Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "ru.magnat.smnavigator.auth";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "ru.magnat.smnavigator";
    // The account name
    public static final String ACCOUNT = "syncAccount";
    // Instance fields
    Account mAccount;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		MainDbHelper.getInstance(getBaseContext());
		
		mAccount = addSyncAccount(this);
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
