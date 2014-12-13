package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.model.StoreStatistics;
import android.util.JsonReader;

public class GetStoreStatisticsHelper {
	
	public List<StoreStatistics> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readStoresArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<StoreStatistics> readStoresArray(JsonReader reader) throws IOException {
		List<StoreStatistics> storeStatistics = new ArrayList<StoreStatistics>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	storeStatistics.add(readStoreStatistics(reader));
	    }
	    reader.endArray();
	    
	    return storeStatistics;
	}

	public StoreStatistics readStoreStatistics(JsonReader reader) throws IOException {
		StoreStatistics storeStatistics = new StoreStatistics();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("id")) {
	    		storeStatistics.setId(reader.nextInt());
	    	} else if (name.equals("last_visit")) {
	    		reader.nextString();
	    		storeStatistics.setLastVisit(new Date(System.currentTimeMillis())); 
	    	} else if (name.equals("next_visit")) {
	    		reader.nextString();
	    		storeStatistics.setNextVisit(new Date(System.currentTimeMillis())); 
	    	} else if (name.equals("total_distribution")) {
	    		storeStatistics.setTotalDistribution(reader.nextDouble()); 
	    	} else if (name.equals("golden_distribution")) {
	    		storeStatistics.setGoldenDistribution(reader.nextDouble()); 
	    	} else if (name.equals("turnover_previous_month")) {
	    		storeStatistics.setTurnoverPreviousMonth(reader.nextDouble());
	    	} else if (name.equals("turnover_current_month")) {
	    		storeStatistics.setTurnoverCurrentMonth(reader.nextDouble()); 
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return storeStatistics;
	}
	
}
