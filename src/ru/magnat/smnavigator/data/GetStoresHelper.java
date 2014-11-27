package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.entities.Store;
import android.util.JsonReader;
import android.util.Log;

public class GetStoresHelper {
	
	public List<Store> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readStoresArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<Store> readStoresArray(JsonReader reader) throws IOException {
		List<Store> stores = new ArrayList<Store>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	stores.add(readStore(reader));
	    }
	    reader.endArray();
	    
	    Log.d("", "Store = " + stores.size()); 
	    
	    return stores;
	}

	public Store readStore(JsonReader reader) throws IOException {
		Store store = new Store();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("id")) {
	    		store.setId(reader.nextInt());
	    	} else if (name.equals("name")) {
	    		store.setName(reader.nextString());
	    	} else if (name.equals("address")) {
	    		store.setAddress(reader.nextString()); 
	    	} else if (name.equals("tel")) {
	    		store.setTel(reader.nextString()); 
	    	} else if (name.equals("channel")) {
	    		store.setChannel(reader.nextString()); 
	    	} else if (name.equals("coverage_type")) {
	    		store.setCoverageType(reader.nextString()); 
	    	} else if (name.equals("golden_status")) {
	    		store.setGoldenStatus(reader.nextString()); 
//	    	} else if (name.equals("psr")) {
//	    		store.setPsr(null); 
//	    	} else if (name.equals("store_statistics")) {
//	    		store.setStoreStatistics(null); 
	    	} else if (name.equals("latitude")) {
	    		store.setLatitude(reader.nextDouble()); 
	    	} else if (name.equals("longitude")) {
	    		store.setLongitude(reader.nextDouble()); 
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return store;
	}
	
}
