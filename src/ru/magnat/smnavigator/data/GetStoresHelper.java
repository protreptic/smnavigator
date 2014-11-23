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
	    
	    Log.d("", "stores loaded = " + stores.size());
	    
	    return stores;
	}

	public Store readStore(JsonReader reader) throws IOException {
		Store store = new Store();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("Id")) {
	    		store.setId(reader.nextInt());
	    	} else if (name.equals("Descr")) {
	    		store.setName(reader.nextString());
	    	} else if (name.equals("Address")) {
	    		store.setAddress(reader.nextString()); 
	    	} else if (name.equals("LocationLat")) {
	    		store.setLatitude(reader.nextDouble()); 
	    	} else if (name.equals("LocationLon")) {
	    		store.setLongitude(reader.nextDouble()); 
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return store;
	}
	
}
