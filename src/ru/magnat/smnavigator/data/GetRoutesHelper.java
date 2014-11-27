package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.entities.Route;
import android.util.JsonReader;
import android.util.Log;

public class GetRoutesHelper {
	
	public List<Route> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readStoresArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<Route> readStoresArray(JsonReader reader) throws IOException {
		List<Route> routes = new ArrayList<Route>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	routes.add(readRoute(reader));
	    }
	    reader.endArray();
	    
	    Log.d("", "Route = " + routes.size()); 
	    
	    return routes;
	}

	public Route readRoute(JsonReader reader) throws IOException {
		Route route = new Route();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("id")) {
	    		route.setId(reader.nextInt());
	    	} else if (name.equals("visit_date")) {
	    		reader.nextString();
	    		route.setVisitDate(new Date(System.currentTimeMillis()));
	    	} else if (name.equals("psr")) {  
	    		route.setPsr(reader.nextInt());
	    	} else if (name.equals("store")) {  
	    		route.setStore(reader.nextInt());
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return route;
	}
	
}
