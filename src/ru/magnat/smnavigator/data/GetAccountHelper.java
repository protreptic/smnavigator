package ru.magnat.smnavigator.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.auth.account.AccountWrapper;
import android.util.JsonReader;

public class GetAccountHelper {
	
	public List<AccountWrapper> readJsonStream(InputStream in) throws IOException {
	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
	     
	     try {
	    	 return readAccountsArray(reader);
	     } finally {
	    	 reader.close();
	     }
	}

	public List<AccountWrapper> readAccountsArray(JsonReader reader) throws IOException {
		List<AccountWrapper> accounts = new ArrayList<AccountWrapper>();

	    reader.beginArray();
	    while (reader.hasNext()) {
	    	accounts.add(readAccount(reader));
	    }
	    reader.endArray();
	    
	    return accounts;
	}

	public AccountWrapper readAccount(JsonReader reader) throws IOException {
		AccountWrapper account = new AccountWrapper();

	    reader.beginObject();
	    while (reader.hasNext()) {
	    	String name = reader.nextName();
	    	
	    	if (name.equals("token")) {
	    		account.setToken(reader.nextString());
	    	} else if (name.equals("expiration")) {
	    		account.setExpiration(reader.nextString());
	    	} else {
	    		reader.skipValue();
	    	}
	    }
	    reader.endObject();

	    return account;
	}
	
}
