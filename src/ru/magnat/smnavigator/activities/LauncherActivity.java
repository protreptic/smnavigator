package ru.magnat.smnavigator.activities;

import java.io.IOException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
 
public class LauncherActivity extends Activity {
	
	private AccountManager mAccountManager;
	private AccountHelper mAccountHelper;
	
	private void checkUpdates() {
		//UpdateHelper.get(this).update();
	}
	
	private void runApplication(Account account, String token) {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("accountToken", token);
		
		startActivity(intent); 
	}
	
	private void addAccount() {
        mAccountManager.addAccount(AccountWrapper.ACCOUNT_TYPE, null, null, null, this, new AccountManagerCallback<Bundle>() {
			
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					
					Log.d("", bundle.toString());
					
					String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
					String accountType = bundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
					
					Account account = new Account(accountName, accountType);
					
					signUp(account);
				} catch (OperationCanceledException e) {
					showChooseAccountDialog();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	@SuppressWarnings("unused")
	private void removeAccount(Account account) {
		mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
			
			@Override
			public void run(AccountManagerFuture<Boolean> future) {
				
			}
		}, null);
	}
	
	private void signUp(Account account) {	
		String accountToken = mAccountManager.peekAuthToken(account, AccountWrapper.ACCOUNT_TYPE);
		
		mAccountManager.invalidateAuthToken(AccountWrapper.ACCOUNT_TYPE, accountToken);
		mAccountManager.getAuthToken(account, AccountWrapper.ACCOUNT_TYPE, null, getParent(), new AccountManagerCallback<Bundle>() {
			
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					
					Log.d("", bundle.toString());
					
					Intent keyIntent = bundle.getParcelable(AccountManager.KEY_INTENT);
					
					if (keyIntent != null) {
						addAccount();
					} else {
						String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
						String accountType = bundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
						String accountToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
						
						Account account = new Account(accountName, accountType);
						
						runApplication(account, accountToken); 
					}
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	private void showChooseAccountDialog() {
		mAccountHelper.refresh();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
        	.setTitle(getString(R.string.chooseUser))
        	.setSingleChoiceItems(mAccountHelper.getAccountListAdapter(), -1, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					if (which == mAccountHelper.getAccounts().size()) {
						addAccount();
					} else {
						Account account = mAccountHelper.getAccounts().get(which);
						
						signUp(account);
					}
				}
			})
			.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
            .show();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		showChooseAccountDialog();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.launcher_activity_layout); 
		
		mAccountManager = AccountManager.get(this);
		mAccountHelper = AccountHelper.get(this);
		
		checkUpdates();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		AccountHelper.release(); 
	}
	
}
