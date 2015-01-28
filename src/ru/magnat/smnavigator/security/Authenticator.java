package ru.magnat.smnavigator.security;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.javaprotrepticon.android.widgetutils.security.account.LoginActivity;
import org.javaprotrepticon.android.widgetutils.security.account.LoginActivity.AuthenticationResponse;

import ru.magnat.smnavigator.security.util.DefaultTrustManager;
import ru.magnat.smnavigator.security.util.FakeHostnameVerifier;
import ru.magnat.smnavigator.security.util.KeyStoreManager;
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
		
		String sessionToken = accountManager.peekAuthToken(account, account.type);
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
		Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtras(options);
        
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(account.type)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_CODE, "invalid authTokenType");
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            
            return result;
        }

        validateSession(mContext, account); 
        
        String syncServerSecure = options.getString("syncServerSecure");
        String certificatePath = options.getString("certificatePath");
        
        AccountManager accountManager = AccountManager.get(mContext);
        
		String accountName = account.name;
		String accountPassword = accountManager.getPassword(account);
		String accountType = account.type;
		String accountToken = null;
        String accountExpiration = null;
		
        if (accountPassword != null) {
        	try {
        		KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(mContext);
        		keyStoreManager.addCertificateFromFile(certificatePath);
        		
        		SSLContext sslContext = SSLContext.getInstance("TLS");
        		sslContext.init(null, new TrustManager[] { new DefaultTrustManager(keyStoreManager.getKeyStore()) } , null);
        		
        		URL url = new URL(String.format(syncServerSecure + "?login=%s&password=%s", accountName, accountPassword));
        		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); 
        		urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        		urlConnection.setHostnameVerifier(new FakeHostnameVerifier()); 
        		
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
        
        Intent intent = new Intent(mContext, LoginActivity.class);
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