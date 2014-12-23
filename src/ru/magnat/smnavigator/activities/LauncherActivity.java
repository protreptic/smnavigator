package ru.magnat.smnavigator.activities;

import java.io.IOException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.update.Artifact;
import ru.magnat.smnavigator.update.CentralRepository;
import ru.magnat.smnavigator.update.DownloadArtifactActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
 
public class LauncherActivity extends Activity {
	
	private AccountManager mAccountManager;
	private AccountHelper mAccountHelper;
	
	private void checkUpdates() {
		new AsyncTask<Void, Void, Artifact>() {

			private static final int NOTIFICATION_ID = 101; 
			
			private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			
			private CentralRepository centralRepository;
			
			protected void onPreExecute() {
				centralRepository = new CentralRepository(getBaseContext()); 
				
				Notification.Builder builder = new Builder(getBaseContext());
				builder.setSmallIcon(R.drawable.logotype_small_icon);
				builder.setContentTitle(getString(R.string.app_name));
				builder.setContentText(getString(R.string.update_check)); 
				builder.setProgress(0, 0, true);
				builder.setAutoCancel(false);
				builder.setOngoing(true);
				
				notificationManager.notify(NOTIFICATION_ID, builder.build()); 
			};
			
			@Override
			protected Artifact doInBackground(Void... params) {
				return centralRepository.update();
			}
			
			protected void onPostExecute(Artifact artifact) {
				notificationManager.cancel(NOTIFICATION_ID);
				
				if (artifact != null) {
					Intent intent = new Intent(getBaseContext(), DownloadArtifactActivity.class);
					intent.putExtra("artifact", artifact);
					
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
					stackBuilder.addParentStack(DownloadArtifactActivity.class);
					stackBuilder.addNextIntent(intent);
					
					PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					
					long[] pattern = {3, 1000, 1000};
					
					Notification.Builder builder = new Builder(getBaseContext());
					builder.setSmallIcon(R.drawable.logotype_small_icon);
					builder.setContentTitle(getString(R.string.app_name));
					builder.setContentText(String.format(getString(R.string.update_update_available), artifact.getVersionName())); 
					builder.setVibrate(pattern);					
					builder.setContentIntent(pendingIntent);
					
					notificationManager.notify(NOTIFICATION_ID, builder.build()); 
				} else {
					Notification.Builder builder = new Builder(getBaseContext());
					builder.setSmallIcon(R.drawable.logotype_small_icon);
					builder.setContentTitle(getString(R.string.app_name));
					builder.setContentText(getString(R.string.update_update_unavailable)); 
					builder.setAutoCancel(false);
					
					notificationManager.notify(NOTIFICATION_ID, builder.build()); 
				}
			};
			
		}.execute(); 
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
		
		// ��������� ����������
		checkUpdates();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		AccountHelper.release(); 
	}
	
}
