package ru.magnat.smnavigator.fragments;

import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	private EditText mLogin;
	private EditText mPassword;
	private Button mSubmit;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.login_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mLogin = (EditText) getActivity().findViewById(R.id.login);
		mLogin.setText("pet");
		
		mPassword = (EditText) getActivity().findViewById(R.id.password);
		mPassword.setText("123"); 
		
		mSubmit = (Button) getActivity().findViewById(R.id.submit);
		mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String login = mLogin.getText().toString();
				String password = mPassword.getText().toString();
				
				if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
					new LoginTask().execute();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("");
					builder.setMessage(getResources().getString(R.string.bad_login_password1));
					builder.setCancelable(false);
					builder.setNegativeButton(getResources().getString(R.string.i_got_it), null);
					builder.create().show();
				}
			}
		});
	}
	
	private class LoginTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mProgressDialog;
		
		private boolean mAccepted = false;
		
		public LoginTask() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setTitle("");
			mProgressDialog.setMessage(getResources().getString(R.string.please_wait));  
			mProgressDialog.setCancelable(false); 
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {			
				TimeUnit.SECONDS.sleep(3);
				
				String login = mLogin.getText().toString();
				String password = mPassword.getText().toString();
				
				if (login.equals("pet") && password.equals("123")) {
					mAccepted = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.hide();
			mProgressDialog = null;
			
			if (mAccepted) {
				Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_LONG).show();
				
				getActivity().getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.conteiner, new NavigationCompositeFragment())
					.commit();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("");
				builder.setMessage(getResources().getString(R.string.bad_login_password2));
				builder.setCancelable(false);
				builder.setNegativeButton(getResources().getString(R.string.i_got_it), null);
				builder.create().show();
			}
		}
		
	}
	
}
