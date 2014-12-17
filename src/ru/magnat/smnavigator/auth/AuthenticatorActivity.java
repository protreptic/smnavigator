package ru.magnat.smnavigator.auth;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.util.Fonts;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
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
	
    private static final String TAG = "AuthenticatorActivity";
    
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    
    private AccountManager mAccountManager;

    private UserLoginTask mAuthTask;

    private TextView mMessageTextView;
	private EditText mLoginEditText;
	private EditText mPasswordEditText;
	private Button mSubmitButton;
	
	private String mUsername;
	private String mPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);
		
		mAccountManager = AccountManager.get(this);
		
		mMessageTextView = (TextView) findViewById(R.id.message);
		mMessageTextView.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		mMessageTextView.setTextSize(32); 
		mMessageTextView.setTextColor(getResources().getColor(R.color.red)); 
		
		mLoginEditText = (EditText) findViewById(R.id.login); 
		mLoginEditText.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		mLoginEditText.setTextSize(32); 
		
		mPasswordEditText = (EditText) findViewById(R.id.password);
		mPasswordEditText.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		mPasswordEditText.setTextSize(32); 
		
		mSubmitButton = (Button) findViewById(R.id.submit);
		mSubmitButton.setTypeface(Fonts.getInstance(this).getTypeface("RobotoCondensed-Regular"));
		mSubmitButton.setTextSize(32); 
		mSubmitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
		        mUsername = mLoginEditText.getText().toString();
		        mPassword = mPasswordEditText.getText().toString();
		        
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
            
            mMessageTextView.setText("Please enter a valid username/password.");
            
            mLoginEditText.requestFocus();
            mLoginEditText.setText(""); 
            mPasswordEditText.setText(""); 
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
        		return Authenticator.authenticate(getBaseContext(), mUsername, mPassword); 
        	} catch (Exception e) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, e.toString());
        		
        		return null;
        	}
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
