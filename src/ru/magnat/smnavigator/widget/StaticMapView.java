package ru.magnat.smnavigator.widget;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.entities.Mappable;
import ru.magnat.smnavigator.util.Crypto;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class StaticMapView extends RelativeLayout {

	public StaticMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StaticMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StaticMapView(Context context) {
		super(context);
	}

	private static final String STATIC_MAP_CACHE = "static_map_cache";
	
	public static Map<String, Drawable> drawables = new HashMap<String, Drawable>();
	
	private Mappable mappable;
	private Drawable unavailable;
	private Drawable drawable;
	private String hash;
	private String path;
	
	public void setMappable(Mappable mappable) {
		this.mappable = mappable;
		this.unavailable = getResources().getDrawable(R.drawable.map_unavailable);
		
		if (mappable.getLatitude() == 0 && mappable.getLongitude() == 0) {
			ImageView unavailbleDrawable = new ImageView(getContext());
			
			unavailbleDrawable.setImageDrawable(unavailable);   
			
			addView(unavailbleDrawable); return;
		}
		
		this.hash = Crypto.getMd5HashFromString("" + mappable.getLatitude() + mappable.getLongitude());
		this.path = getContext().getCacheDir().getAbsolutePath() + "/" + hash;
		
		ImageView imageView = new ImageView(getContext());
		imageView.setBackground(unavailable); 
		
		if (drawables.get(hash) != null) {
			drawable = drawables.get(hash);   
			
			imageView.setImageDrawable(drawable);   
			
			addView(imageView); 
			
			Log.d(STATIC_MAP_CACHE, "cache:memory(" + drawables.size() + "):loaded->" + hash); return;
		}
		
		if (new File(path).exists()) {
			drawable = Drawable.createFromPath(path);
			
			if (drawables.size() >= 150) {
				drawables.clear();
				drawables = null;
				
				drawables = new HashMap<String, Drawable>(150);
				
				Log.d(STATIC_MAP_CACHE, "cache:memory:clear"); return;
			}
			
			if (!drawables.containsKey(hash)) {
				drawables.put(hash, drawable); 
			}
			
			imageView.setImageDrawable(drawable);   
			
			addView(imageView); 
			
			Log.d(STATIC_MAP_CACHE, "cache:disk:loaded->" + hash); return;
		}
		
		new StaticMapLoader().execute(); return;
	}
	
	private class StaticMapLoader extends AsyncTask<Void, Void, Drawable> {
		
		private static final String TAG = "static_map_loader";
		
		@Override
		protected void onPreExecute() {
			removeAllViews();
			
			LayoutParams layoutParams = new LayoutParams(100, 100);
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
			urlBuilder.append("https://maps.googleapis.com/maps/api/staticmap?");
			urlBuilder.append("center=" + mappable.getLatitude() + "," + mappable.getLongitude());
			urlBuilder.append("&zoom=16");
			urlBuilder.append("&size=100x100");
			urlBuilder.append("&scale=1");
			urlBuilder.append("&sensor=false");
			urlBuilder.append("&markers=size:tiny|" + mappable.getLatitude() + "," + mappable.getLongitude());
			urlBuilder.append("&region=RU");
			urlBuilder.append("&language=ru");
			urlBuilder.append("&key=AIzaSyDNbE0ami4vsDvdzoAdr_8AGS1DlUTl5Mw");
			
			Log.d(TAG, "get->" + urlBuilder.toString());
			
			try {
				drawable = Drawable.createFromPath(readDataFromUrl(urlBuilder.toString())); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return drawable;
		}
		
		private String readDataFromUrl(String url) throws MalformedURLException, IOException {		
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
			
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
			
			return path;
		} 
		
		@Override
		protected void onPostExecute(Drawable drawable) {
			removeAllViews();
			
			ImageView imageView = new ImageView(getContext());
			imageView.setBackground(unavailable); 
			
			if (drawable != null) 
				imageView.setImageDrawable(drawable); 
			  
			addView(imageView); 
		}
		
	}
	
}
