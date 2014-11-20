package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class UpdateStoresTask extends AsyncTask<Void, Void, Void> {
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			URL url = new URL("http://sfs.magnat.ru:8081/sm_get_outlets");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			GetStoresHelper storesHelper = new GetStoresHelper();
			storesHelper.readJsonStream(urlConnection.getInputStream());
			urlConnection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
