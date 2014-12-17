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
import android.content.SharedPreferences;
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
				
				SharedPreferences settings = context.getSharedPreferences("global", Context.MODE_MULTI_PROCESS);
				
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putString("defaultAccountName", login);
				editor.putString("defaultAccountType", AccountWrapper.ACCOUNT_TYPE); 
				editor.putString("defaultAccountPassword", password);
				editor.putString("defaultAccountSessionToken", account.getToken());
				editor.putString("defaultAccountSessionTokenExpiration", account.getExpiration());
				
				editor.commit();
				
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
        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountWrapper.ACCOUNT_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final String password = mAccountManager.getPassword(account);
        if (password != null) {
            final String authToken = authenticate(mContext, account.name, password); 
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountWrapper.ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
        intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        
        final Bundle bundle = new Bundle();
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