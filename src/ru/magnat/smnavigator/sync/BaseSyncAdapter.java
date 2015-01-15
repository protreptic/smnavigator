package ru.magnat.smnavigator.sync;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.javaprotrepticon.android.widgetutils.security.DefaultTrustManager;
import org.javaprotrepticon.android.widgetutils.security.FakeHostnameVerifier;
import org.javaprotrepticon.android.widgetutils.security.KeyStoreManager;

import ru.magnat.smnavigator.account.Authenticator;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

public abstract class BaseSyncAdapter extends AbstractThreadedSyncAdapter {
    
	protected Account mAccount;
    
	private String mSessionToken;
	private SSLSocketFactory mSSLSocketFactory;
	private HostnameVerifier mHostnameVerifier = new FakeHostnameVerifier();
	
	private Timer mAckTimer;
	
	protected void startAck() {
		mAckTimer = new Timer("ackSender");
		mAckTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ack");
			}
		}, 0, 1500);
	}
	
	protected void stopAck() {
		mAckTimer.cancel();
	}
	
	protected HttpsURLConnection prepareConnection(String serviceName) throws Exception {
		URL url = new URL(getServerName() + serviceName + "?token=" + mSessionToken);
		 
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection(); 
		httpsURLConnection.setSSLSocketFactory(mSSLSocketFactory);
		httpsURLConnection.setHostnameVerifier(mHostnameVerifier); 
		
		return httpsURLConnection;
	}
	
	public abstract String getServerName();
	public abstract String getActionTag();
	
    public BaseSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    
    public BaseSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;  
    	
    	Authenticator.validateSession(getContext(), account);
    	
    	mSessionToken = AccountManager.get(getContext()).peekAuthToken(account, account.type);
    	
    	String certificatePath = extras.getString("certificatePath");
		
		try {
			KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(getContext());
			keyStoreManager.addCertificateFromFile(certificatePath);
			
			TrustManager[] trustManager = new TrustManager[] { new DefaultTrustManager(keyStoreManager.getKeyStore()) };
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManager, null);
			
			mSSLSocketFactory = sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
    }
    
	protected void sendNotification(String action) {
    	Intent intentStarted = new Intent(getActionTag());
    	intentStarted.putExtra("action", action);
    	intentStarted.putExtra("account", mAccount.name);
    	
    	getContext().sendBroadcast(intentStarted);
    }
	
	@Override
	public void onSyncCanceled() {
		super.onSyncCanceled();
		
		sendNotification("canceled"); 
	}

	@Override
	public void onSyncCanceled(Thread thread) {
		super.onSyncCanceled(thread);
		
		sendNotification("canceled"); 
	}

}
