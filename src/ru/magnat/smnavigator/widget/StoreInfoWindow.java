package ru.magnat.smnavigator.widget;

import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.view.StoreView;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class StoreInfoWindow implements InfoWindowAdapter {

	private Context context;
	private Store store;
	
	public StoreInfoWindow(Context context, Store store) {
		this.context = context;
		this.store = store;
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		Store store = new Store();
		store.setName(""); 
		store.setCustomer(marker.getTitle());
		store.setAddress(marker.getSnippet()); 
		store.setChannel("");
		store.setGoldenStatus("");
		store.setLatitude(.0);
		store.setLongitude(.0); 
				
		return new StoreView(context, store); 
	}
	
}
