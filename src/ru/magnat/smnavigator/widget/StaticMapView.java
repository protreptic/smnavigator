package ru.magnat.smnavigator.widget;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import ru.magnat.smnavigator.util.Crypto;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class StaticMapView extends RelativeLayout {

	private Store store;
	
	public StaticMapView(Context context, Store store) { 
		super(context); this.store = store;
		
		setPadding(5, 5, 5, 5); 
		
		if ((StoreListFragment.drawables.get(store.getId())) != null) {
			ImageView imageView = new ImageView(getContext());
			imageView.setImageDrawable(StoreListFragment.drawables.get(store.getId()));  
			
			addView(imageView); 
		} else {
			new StaticMapLoader().execute();
		}
	}
	
	private class StaticMapLoader extends AsyncTask<Void, Void, Drawable> {
		
		@Override
		protected void onPreExecute() {
			removeAllViews();
			
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT); 
			 
			ProgressBar progressBar = new ProgressBar(getContext());
			progressBar.setIndeterminate(true); 
			progressBar.setLayoutParams(layoutParams); 
			
			addView(progressBar); 
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			Drawable drawable = null;
			
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append("http://maps.googleapis.com/maps/api/staticmap?");
			urlBuilder.append("center=" + store.getLatitude() + "," + store.getLongitude());
			urlBuilder.append("&zoom=16");
			urlBuilder.append("&size=100x100");
			urlBuilder.append("&scale=1");
			urlBuilder.append("&sensor=false");
			urlBuilder.append("&markers=size:tiny|" + store.getLatitude() + "," + store.getLongitude());
			urlBuilder.append("&region=RU");
			urlBuilder.append("&language=ru");
			
			String hash = Crypto.getMd5HashFromString(urlBuilder.toString());
			
			Log.d("static map loader", "url: " + urlBuilder.toString());
			Log.d("static map loader", "hash: " + hash); 
			
			try {
				TimeUnit.MILLISECONDS.sleep(150);
				
				URL url = new URL(urlBuilder.toString());
				
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
				
				drawable = Drawable.createFromStream(urlConnection.getInputStream(), "");
				
				urlConnection.disconnect();
				urlConnection = null;
				
				if (StoreListFragment.drawables.indexOfKey(store.getId()) < 0) {
					StoreListFragment.drawables.put(store.getId(), drawable);  
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return drawable;
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			removeAllViews();
			
			ImageView imageView = new ImageView(getContext());
			imageView.setImageDrawable(result);  
			  
			addView(imageView); 
		}
		
	}
	
}
