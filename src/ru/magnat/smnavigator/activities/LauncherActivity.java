package ru.magnat.smnavigator.activities;

import java.io.IOException;
import java.sql.Timestamp;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.AccountWrapper;
import ru.magnat.smnavigator.auth.Authenticator;
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class LauncherActivity extends Activity {
	
	private AccountManager mAccountManager;
	
	private void runApplication(Account account) {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
		
		intent.putExtra("account", account);
		
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
					e.printStackTrace();
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
		Authenticator.validateSession(getBaseContext(), account);
 		
		mAccountManager.getAuthToken(account, account.type, null, getParent(), new AccountManagerCallback<Bundle>() {
			
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
						
						@SuppressWarnings("unused")
						String accountToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
						
						Account account = new Account(accountName, accountType);
						
						runApplication(account); 
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
		final AccountListAdapter accountListAdapter = new AccountListAdapter();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
        	.setTitle(null)
        	.setSingleChoiceItems(accountListAdapter, -1, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int position) {
					if (position == accountListAdapter.getCount() - 1) {
						addAccount();
					} else {
						Account account = (Account) accountListAdapter.getItem(position);
						
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
	
	private class AccountListAdapter extends BaseAdapter {

		private Account[] mAccounts;
		
		public AccountListAdapter() {
			mAccounts = mAccountManager.getAccountsByType(AccountWrapper.ACCOUNT_TYPE);
		}
		
		@Override
		public int getCount() {
			return mAccounts.length + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == mAccounts.length) 
				return null;
			
			return mAccounts[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.account_item, parent, false);
				
				Typeface typeface = Fonts.get(getBaseContext()).getTypeface("RobotoCondensed-Regular");
				
				holder = new ViewHolder();
				holder.line1 = (TextView) convertView.findViewById(R.id.line1);
				holder.line1.setTypeface(typeface);
				
				holder.line2 = (TextView) convertView.findViewById(R.id.line2);
				holder.line2.setTypeface(typeface);
				
				convertView.setTag(holder); 
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (position == mAccounts.length) {
				holder.line1.setText(getString(R.string.addUser));
				holder.line2.setText(getString(R.string.addUserDescription));
			} else {
				Account account = (Account) getItem(position);
				
				String accountName = account.name;
				String accountExpire = mAccountManager.getUserData(account, "sessionExpiration");
				
				Timestamp curentTimestamp = new Timestamp(System.currentTimeMillis());
				Timestamp sessionTimestamp = Timestamp.valueOf(accountExpire);
				
				String sessionStatus = (curentTimestamp.before(sessionTimestamp)) ? getString(R.string.sessionActive) : getString(R.string.sessionExpired);
 				
				holder.line1.setText(accountName);
				holder.line2.setText(sessionStatus);  
			}
			
			return convertView;
		}
		
	}
	
	private static class ViewHolder {
		TextView line1;
		TextView line2;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAccountManager = AccountManager.get(this);
	}
	
}
