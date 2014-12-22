package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.model.Manager;
import android.util.JsonReader;

public class GetBranchHelper {
	
	public List<Manager> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readManagersArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<Manager> readManagersArray(JsonReader reader) throws IOException {
		List<Manager> managers = new ArrayList<Manager>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	managers.add(readManager(reader));
	    }
	    reader.endArray();
	    
	    return managers;
	}

	public Manager readManager(JsonReader reader) throws IOException {
		Manager manager = new Manager();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("id")) {
	    		manager.setId(reader.nextInt());
	    	} else if (name.equals("name")) {
	    		manager.setName(reader.nextString());
	    	} else if (name.equals("email")) {
	    		manager.setEmail(reader.nextString());
	    	} else if (name.equals("tel")) {
	    		manager.setTel(reader.nextString());
	    	} else if (name.equals("branch")) {
	    		manager.setBranch(reader.nextInt());
	    	} else if (name.equals("department")) {
	    		manager.setDepartment(reader.nextInt());
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return manager;
	}
	
}
