package ru.magnat.smnavigator.security.account;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.security.KeyStoreManager;
import ru.magnat.smnavigator.security.MyTrustManager;
import ru.magnat.smnavigator.security.account.SignInActivity.AuthenticationResponse;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class Authenticator extends AbstractAccountAuthenticator {
	
	private Context mContext;
	
	public Authenticator(Context context) {
		super(context);
		
		mContext = context;
	}

	public static void validateSession(Context context, Account account) {
		AccountManager accountManager = AccountManager.get(context);
		
		String sessionToken = accountManager.peekAuthToken(account, AccountWrapper.ACCOUNT_TYPE);
		String sessionExpires = accountManager.getUserData(account, "sessionExpiration");
		
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Timestamp sessionTimestamp = Timestamp.valueOf(sessionExpires);
		
		if (currentTimestamp.after(sessionTimestamp)) {
			accountManager.invalidateAuthToken(account.type, sessionToken); 
			accountManager.clearPassword(account);
		}
	}
	
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
         
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(AccountWrapper.ACCOUNT_TYPE)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_CODE, "invalid authTokenType");
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            
            return result;
        }

        validateSession(mContext, account); 
        
        AccountManager accountManager = AccountManager.get(mContext);
        
		String accountName = account.name;
		String accountPassword = accountManager.getPassword(account);
		String accountType = account.type;
		String accountToken = null;
        String accountExpiration = null;
		
        if (accountPassword != null) {
        	try {
        		SSLContext sslContext = SSLContext.getInstance("TLS");
        		sslContext.init(null, new TrustManager[] { new MyTrustManager(KeyStoreManager.getInstance(mContext).getKeyStore()) } , null);
        		
        		HostnameVerifier hostnameVerifier = new HostnameVerifier () {

    				@Override
    				public boolean verify(String hostname, SSLSession session) {
    					return true;
    				}
    				
        		};
        		
        		URL url = new URL(String.format(mContext.getString(R.string.syncServerSecure) + "/sm_auth2?login=%s&password=%s", accountName, accountPassword));
        		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); 
        		urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        		urlConnection.setHostnameVerifier(hostnameVerifier); 
        		
    			GsonBuilder gsonBuilder = new GsonBuilder();
    			gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
    			gsonBuilder.serializeNulls();
    			
    			Gson gson = gsonBuilder.create();

    			AuthenticationResponse authenticationResponse = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream())), AuthenticationResponse.class);
        		
        		urlConnection.disconnect();
        		urlConnection = null;
    			
    			accountToken = authenticationResponse.getSessionToken();
    			accountExpiration = authenticationResponse.getSessionExpiration().toString();
        		
    			accountManager.setAuthToken(account, account.type, accountExpiration); 
    			accountManager.setUserData(account, "sessionExpiration", accountExpiration); 
        		
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                result.putString(AccountManager.KEY_AUTHTOKEN, accountToken);

                return result;
        	} catch (MalformedURLException e) { 
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle bundle) throws NetworkErrorException {
		return null;
	}
	
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	
}