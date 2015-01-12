package ru.magnat.smnavigator.auth;

import java.sql.Timestamp;

import org.apache.http.Header;
import org.javaprotrepticon.android.androidutils.Fonts;
import org.json.JSONObject;

import ru.magnat.smnavigator.R;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SignInActivity extends AccountAuthenticatorActivity {
	
	private LinearLayout mSingInFormWapper;
	private EditText mLoginField;
	private EditText mPasswordField;
	private TextView mSignInButton;
	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.signin_activity); 
		
		mSingInFormWapper = (LinearLayout) findViewById(R.id.signInFormWapper);
		
		Typeface typeface = Fonts.get(this).getTypeface("RobotoCondensed-Light");
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		mLoginField = (EditText) findViewById(R.id.loginField); 
		mLoginField.setText(""); 
		mLoginField.setTypeface(typeface);
		
		mPasswordField = (EditText) findViewById(R.id.passwordField);
		mPasswordField.setTypeface(typeface);
		
		mSignInButton = (TextView) findViewById(R.id.signInButton);
		mSignInButton.setTypeface(typeface);
		mSignInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				final String login = mLoginField.getText().toString();
				final String password = mPasswordField.getText().toString();
				
				if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) { 
					Toast.makeText(getBaseContext(), getString(R.string.authEmptyCredentials), Toast.LENGTH_LONG).show(); return;
				}
				
				hideKeyboard();
				
				String requestUrl = getString(R.string.syncServer) + "/sm_auth2";
				
				RequestParams requestParams = new RequestParams();
				requestParams.put("login", login);
				requestParams.put("password", password);
				
				new AsyncHttpClient().get(requestUrl, requestParams, new JsonHttpResponseHandler() {
					
					@Override
					public void onStart() {
						mSingInFormWapper.setVisibility(View.GONE); 
						mProgressBar.setVisibility(View.VISIBLE); 
					}
					
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
						gsonBuilder.serializeNulls();
						
						Gson gson = gsonBuilder.create();

						AuthenticationResponse authenticationResponse = gson.fromJson(response.toString(), AuthenticationResponse.class);
						
						String accountName = login;
						String accountPassword = password;
						String accountType = AccountWrapper.ACCOUNT_TYPE;
						String accountToken = authenticationResponse.getSessionToken();
						
						AccountManager accountManager = AccountManager.get(getBaseContext());
						
						Account account = new Account(accountName, accountType);
				        
				        accountManager.addAccountExplicitly(account, accountPassword, null);
				        accountManager.setPassword(account, accountPassword); 
				        accountManager.setUserData(account, "sessionExpiration", authenticationResponse.getSessionExpiration().toString());
				        accountManager.setAuthToken(account, accountType, accountToken); 
				        
				        Intent intent = new Intent();
				        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
				        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
				        
				        setAccountAuthenticatorResult(intent.getExtras());
				        setResult(RESULT_OK, intent);
				        
				        finish();
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						mPasswordField.setText(""); 
						
						mProgressBar.setVisibility(View.GONE);
						mSingInFormWapper.setVisibility(View.VISIBLE); 
						
						hideKeyboard();
						
						Toast.makeText(getBaseContext(), getString(R.string.authEmptyCredentials), Toast.LENGTH_LONG).show();
					} 
					
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						mPasswordField.setText(""); 
						
						mProgressBar.setVisibility(View.GONE);
						mSingInFormWapper.setVisibility(View.VISIBLE); 
						
						hideKeyboard();
						
						Toast.makeText(getBaseContext(), getString(R.string.authEmptyCredentials), Toast.LENGTH_LONG).show();
					}
					
				});
			}
		});
		
		hideKeyboard();
	}
	
	private void hideKeyboard() {
	    View view = getCurrentFocus();
	    
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	public class AuthenticationResponse {
		
		private String sessionToken;
		private Timestamp sessionExpiration;
				
		public String getSessionToken() {
			return sessionToken;
		}

		public void setSessionToken(String sessionToken) {
			this.sessionToken = sessionToken;
		}

		public Timestamp getSessionExpiration() {
			return sessionExpiration;
		}

		public void setSessionExpiration(Timestamp sessionExpiration) {
			this.sessionExpiration = sessionExpiration;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " [sessionToken=" + sessionToken + ", sessionExpiration=" + sessionExpiration + "]";
		}
		
	}
	
}
