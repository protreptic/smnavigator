package ru.magnat.smnavigator.widget;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.javaprotrepticon.android.androidutils.Crypto;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.base.Mappable;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class StaticMapView extends ImageView {

	public StaticMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public StaticMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public StaticMapView(Context context) {
		super(context);
	
		init();
	}

	private void init() {
		imageSize = imageSize();
		
		setLayoutParams(new LayoutParams(imageSize, imageSize)); 
	}
	
	public static Map<String, Drawable> sDrawableCache = new HashMap<String, Drawable>();
	
	private Mappable mappable;
	private static final int IMAGE_LOCATION_UNAVAILABLE = R.drawable.map_unavailable;

	private String hash;
	private String path;
	
	private int imageSize = 50;
	
	private int imageSize() {
		Configuration config = getResources().getConfiguration();
		
		int screenSize = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			imageSize = 50;
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			imageSize = 75;
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
		    imageSize = 75;
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			imageSize = 100;
		} 
		
		return imageSize;
	}
	
	public void setMappable(Mappable mappable) {
		this.mappable = mappable;

		new ImageLoader().execute();
	}
	
	private class ImageLoader extends AsyncTask<Void, Void, Drawable> {
		
		@Override
		protected void onPreExecute() {
			setImageDrawable(null); 
		}
		
		@Override
		protected Drawable doInBackground(Void... params) {
			// Если координаты недоступны
			if (mappable.getLatitude() == 0 && mappable.getLongitude() == 0) {
				return getResources().getDrawable(IMAGE_LOCATION_UNAVAILABLE); 
			}
			
			hash = Crypto.getMd5HashFromString("" + mappable.getLatitude() + mappable.getLongitude());
			
			if (sDrawableCache.containsKey(hash)) {
				return sDrawableCache.get(hash);   
			}
			
			path = getContext().getCacheDir().getAbsolutePath() + "/" + hash;
			
			Drawable drawable = null;
			
			if (new File(path).exists()) {
				drawable = Drawable.createFromPath(path);
				
				if (sDrawableCache.size() >= 100) {
					sDrawableCache.clear();
					sDrawableCache = new HashMap<String, Drawable>(150);
				}
				
				if (!sDrawableCache.containsKey(hash)) {
					sDrawableCache.put(hash, drawable); 
				}
				
				return drawable; 
			}
			
			if ((drawable = Drawable.createFromPath(readDataFromUrl())) == null) {
				return getResources().getDrawable(IMAGE_LOCATION_UNAVAILABLE); 
			}
			
			return drawable; 
		}
		
//		private void loadImage() {
//			String apiKey = null;
//			
//			try {
//				apiKey = getContext().getPackageManager().getPackageInfo("ru.magnat.smnavigator", PackageManager.GET_META_DATA).applicationInfo.metaData.getString("com.google.android.maps.v2.API_KEY");
//			} catch (NameNotFoundException e) {
//				e.printStackTrace();
//			}
//			
//			RequestParams requestParams = new RequestParams();
//			requestParams.add("center", mappable.getLatitude() + "," + mappable.getLongitude());
//			requestParams.add("zoom", "16");
//			requestParams.add("size", imageSize + "x" + imageSize);
//			requestParams.add("scale", "1");
//			requestParams.add("sensor", "false");
//			requestParams.add("markers", "size:tiny|" + mappable.getLatitude() + "," + mappable.getLongitude());
//			requestParams.add("region", "RU");
//			requestParams.add("language", "ru");
//			requestParams.add("key", apiKey);
//			
//			new SyncHttpClient().get("https://maps.googleapis.com/maps/api/staticmap", requestParams, new BinaryHttpResponseHandler() {
//				
//				@Override
//				public void onSuccess(int responseCode, Header[] headers, byte[] data) {
//					
//				}
//				
//				@Override
//				public void onFailure(int responseCode, Header[] headers, byte[] data, Throwable throwable) {
//					
//				}
//
//			});
//		}
		
		private String readDataFromUrl() {		
			try {
				String apiKey = getContext().getPackageManager().getPackageInfo("ru.magnat.smnavigator", PackageManager.GET_META_DATA).applicationInfo.metaData.getString("com.google.android.maps.v2.API_KEY");
				 				
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder.append("https://maps.googleapis.com/maps/api/staticmap?");
				urlBuilder.append("center=" + mappable.getLatitude() + "," + mappable.getLongitude());
				urlBuilder.append("&zoom=16");
				urlBuilder.append("&size=" + imageSize + "x" + imageSize);
				urlBuilder.append("&scale=1");
				urlBuilder.append("&sensor=false");
				urlBuilder.append("&markers=size:tiny|" + mappable.getLatitude() + "," + mappable.getLongitude());
				urlBuilder.append("&region=RU");
				urlBuilder.append("&language=ru");
				urlBuilder.append("&key=" + apiKey);
				
				Log.d("", "get->" + urlBuilder.toString());
				
				HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
				
				DataInputStream dis = new DataInputStream(new BufferedInputStream(urlConnection.getInputStream()));
				FileOutputStream fos = new FileOutputStream(new File(path));  
						
				byte[] buffer = new byte[256];
				int count = 0;
				
				while ((count = dis.read(buffer)) != -1) {
					fos.write(buffer, 0, count); 
				}
				
				dis.close();
				fos.close();
				
				urlConnection.disconnect();
				urlConnection = null;
			} catch (Exception e) {
				e.printStackTrace();
				
				return null;
			}
			
			return path;
		} 
		
		@Override
		protected void onPostExecute(Drawable drawable) {
			setImageDrawable(drawable == null ? getResources().getDrawable(IMAGE_LOCATION_UNAVAILABLE) : drawable); 
		}
		
	}
	
}
