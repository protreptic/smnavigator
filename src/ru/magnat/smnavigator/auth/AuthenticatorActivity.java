package ru.magnat.smnavigator.auth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.data.GetAccountHelper;
import ru.magnat.smnavigator.util.Fonts;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
	
    /** The tag used to log to adb console. */
    private static final String TAG = "AuthenticatorActivity";
    
    /** The Intent flag to confirm credentials. */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /** The Intent extra to store password. */
    public static final String PARAM_PASSWORD = "password";

    /** The Intent extra to store username. */
    public static final String PARAM_USERNAME = "username";

    /** The Intent extra to store username. */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    
    private AccountManager mAccountManager;

    /** Keep track of the login task so can cancel it if requested */
    private UserLoginTask mAuthTask = null;

    private TextView message;
	private EditText login;
	private EditText password;
	private Button submit;
	
	private String mUsername;
	private String mPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);
		
		mAccountManager = AccountManager.get(this);
		
		message = (TextView) findViewById(R.id.message);
		message.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		message.setTextSize(32); 
		message.setTextColor(getResources().getColor(R.color.red)); 
		
		login = (EditText) findViewById(R.id.login); 
		login.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		login.setTextSize(32); 
		
		password = (EditText) findViewById(R.id.password);
		password.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		password.setTextSize(32); 
		
		submit = (Button) findViewById(R.id.submit);
		submit.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		submit.setTextSize(32); 
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
		        mUsername = login.getText().toString();
		        mPassword = password.getText().toString();
		        
		        mAuthTask = new UserLoginTask();
		        mAuthTask.execute();
			}
			
		});
	}
	
    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. We store the
     * authToken that's returned from the server as the 'password' for this
     * account - so we're never storing the user's actual password locally.
     *
     * @param result the confirmCredentials result.
     */
    private void finishLogin(String authToken) {
        Log.i(TAG, "finishLogin()");
        
        Account account = new Account(mUsername, AccountWrapper.ACCOUNT_TYPE);
        
        mAccountManager.addAccountExplicitly(account, mPassword, null);
        
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountWrapper.ACCOUNT_TYPE);
        
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken the authentication token returned by the server, or NULL if
     *            authentication failed.
     */
    public void onAuthenticationResult(String authToken) {
        boolean success = !TextUtils.isEmpty(authToken);
        Log.i(TAG, "onAuthenticationResult(" + success + ")");

        if (success) {
        	finishLogin(authToken);
        } else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
            
            message.setText("Please enter a valid username/password.");
            
            login.requestFocus();
            login.setText(""); 
            password.setText(""); 
        }
        
        // Our task is complete, so clear it out
        mAuthTask = null;
    }

    public void onAuthenticationCancel() {
        Log.i(TAG, "onAuthenticationCancel()");

        // Our task is complete, so clear it out
        mAuthTask = null;
    }
    
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
        	try {
        		return authenticate(mUsername, mPassword); 
        	} catch (Exception e) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, e.toString());
        		
        		return null;
        	}
        }
        
        private String authenticate(String login, String password) throws NotFoundException, IOException, SQLException {
        	String token = null;
        	
        	try {
        		URL url = new URL("http://" + getResources().getString(R.string.syncServer) + "/sm_auth?login=" + login + "&password=" + password);
        		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

        		List<AccountWrapper> accounts = new GetAccountHelper().readJsonStream(urlConnection.getInputStream());
        		
        		for (AccountWrapper account : accounts) {
					token = account.getToken();
				}
        		
        		urlConnection.disconnect();
        		urlConnection = null;
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
			return token;
        }
        
        @Override
        protected void onPostExecute(final String authToken) {
            // On a successful authentication, call back into the Activity to
            // communicate the authToken (or null for an error).
            onAuthenticationResult(authToken);
        }

        @Override
        protected void onCancelled() {
            // If the action was canceled (by the user clicking the cancel
            // button in the progress dialog), then call back into the
            // activity to let it know.
            onAuthenticationCancel();
        }
        
    }
    
    
	
}
