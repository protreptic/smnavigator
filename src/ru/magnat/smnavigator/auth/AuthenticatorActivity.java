package ru.magnat.smnavigator.auth;

import java.util.concurrent.TimeUnit;

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
		mMessageTextView.setTextSize(30); 
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
	
    private void finishLogin(String authToken) {
        Log.i(TAG, "finishLogin()");
        
        Account account = new Account(mUsername, AccountWrapper.ACCOUNT_TYPE);
        
        Account[] accounts = mAccountManager.getAccountsByType(AccountWrapper.ACCOUNT_TYPE);
        
        boolean accountExists = false;
        
        for (Account temp : accounts) {
			if (temp.name.equals(account.name)) {
				mAccountManager.setPassword(account, mPassword); 
				
				accountExists = true;
				
				break;
			}
		} 
        
        if (!accountExists) {
        	mAccountManager.addAccountExplicitly(account, mPassword, null);
        }
        
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
            
            mMessageTextView.setText(getString(R.string.authError));
            
            mLoginEditText.requestFocus();
            //mLoginEditText.setText(""); 
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
        		TimeUnit.SECONDS.sleep(3);
        		
        		return Authenticator.authenticate(getBaseContext(), mUsername, mPassword); 
        	} catch (Exception e) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, e.toString());
        		
        		return null;
        	}
        }
        
        @Override
        protected void onPostExecute(String authToken) {
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
