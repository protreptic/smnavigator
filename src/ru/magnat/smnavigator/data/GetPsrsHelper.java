package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.model.Psr;
import android.util.JsonReader;

public class GetPsrsHelper {
	
	public List<Psr> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readStoresArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<Psr> readStoresArray(JsonReader reader) throws IOException {
		List<Psr> psrs = new ArrayList<Psr>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	psrs.add(readPsr(reader));
	    }
	    reader.endArray();
	    
	    return psrs;
	}

	public Psr readPsr(JsonReader reader) throws IOException {
		Psr psr = new Psr();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("id")) {
	    		psr.setId(reader.nextInt());
	    	} else if (name.equals("name")) {
	    		psr.setName(reader.nextString());
	    	} else if (name.equals("project")) {
	    		psr.setProject(reader.nextString()); 
	    	} else if (name.equals("tel")) {
	    		psr.setTel(reader.nextString()); 
	    	} else if (name.equals("branch")) {
	    		psr.setBranch(reader.nextString()); 
	    	} else if (name.equals("department")) {
	    		psr.setDepartment(reader.nextString()); 
	    	} else if (name.equals("latitude")) {
	    		psr.setLatitude(reader.nextDouble()); 
	    	} else if (name.equals("longitude")) {
	    		psr.setLongitude(reader.nextDouble()); 
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return psr;
	}
	
}
