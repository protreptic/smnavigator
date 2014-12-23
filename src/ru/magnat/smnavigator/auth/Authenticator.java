package ru.magnat.smnavigator.auth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.data.GetAccountHelper;
import ru.magnat.smnavigator.security.KeyStoreManager;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {
	
	private Context mContext;
	private AccountManager mAccountManager;
	
	public Authenticator(Context context) {
		super(context);
		
		mContext = context;
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

    public static String authenticate(Context context, String login, String password) {
    	String token = null;
    	
    	try {
    		// Create an SSLContext that uses our TrustManager
    		SSLContext sslContext = SSLContext.getInstance("TLS");
    		sslContext.init(null, new TrustManager[] { new MyTrustManager(KeyStoreManager.getInstance(context).getKeyStore()) } , null);
    		
    		HostnameVerifier hostnameVerifier = new HostnameVerifier () {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
				
    		};
    		
    		URL url = new URL(String.format(context.getString(R.string.syncServerSecure) + "/sm_auth?login=%s&password=%s", login, password));
    		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); 
    		urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
    		urlConnection.setHostnameVerifier(hostnameVerifier); 
    		
    		List<AccountWrapper> accounts = new GetAccountHelper().readJsonStream(urlConnection.getInputStream());
    		
    		for (ru.magnat.smnavigator.auth.account.AccountWrapper account : accounts) {
				token = account.getToken();
				
				Log.d("", account.toString());
			}
    		
    		urlConnection.disconnect();
    		urlConnection = null;
    	} catch (MalformedURLException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return token;
    }
	    
	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(AccountWrapper.ACCOUNT_TYPE)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_CODE, "invalid authTokenType");
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            
            return result;
        }

		String accountName = account.name;
		String accountPassword = mAccountManager.getPassword(account);
		String accountType = account.type;
		String accountToken = null;
        
        if (accountPassword != null) {
            accountToken = authenticate(mContext, accountName, accountPassword); 
            
            if (!TextUtils.isEmpty(accountToken)) {
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                result.putString(AccountManager.KEY_AUTHTOKEN, accountToken);

                return result;
            }
        }
        
        Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	// Getting a label for the auth token is not supported
	@Override
	public String getAuthTokenLabel(String authTokenType) {
		throw new UnsupportedOperationException();
	}

	// Editing properties is not supported
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		throw new UnsupportedOperationException();
	}

	// Ignore attempts to confirm credentials
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle bundle) throws NetworkErrorException {
		return null;
	}
	
	// Updating user credentials is not supported
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

	// Checking features for the account is not supported
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	
}