package ru.magnat.smnavigator.activities;

import java.io.IOException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.account.AccountHelper;
import ru.magnat.smnavigator.account.AccountWrapper;
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

public class LauncherActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.endless_list_background));  
		
        final AccountHelper accountHelper = AccountHelper.getInstance(this);
        
        if (accountHelper.getAccounts().isEmpty()) {
            AccountManager.get(this).addAccount(AccountWrapper.ACCOUNT_TYPE, null, null, null, this, new AccountManagerCallback<Bundle>() {
				
				@Override
				public void run(AccountManagerFuture<Bundle> future) {
					try {
						Bundle bundle = future.getResult();
						
						if (!accountHelper.getAccounts().isEmpty()) {
							Account account = new Account(bundle.getString(AccountManager.KEY_ACCOUNT_NAME), AccountManager.KEY_ACCOUNT_TYPE); 
							
							launchApplication(account);
						}
					} catch (OperationCanceledException e) {
						e.printStackTrace();
						finish();
					} catch (AuthenticatorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, null);
        } else {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder
	        	.setTitle(null)
	        	.setSingleChoiceItems(accountHelper.getAccountListAdapter(), -1, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						Account account = accountHelper.getAccounts().get(which);
						 
						launchApplication(account);
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
	}
	
	private void launchApplication(Account account) {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		
		startActivity(intent); 
	}
	
}
