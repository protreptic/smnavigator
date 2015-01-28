package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Store;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StoreSerializer implements JsonSerializer<Store> {

	@Override
	public JsonElement serialize(Store store, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", store.getId());
		jsonObject.addProperty("name", store.getName());
		jsonObject.add("customer", context.serialize(store.getCustomer()));
		jsonObject.addProperty("address", store.getAddress());
		jsonObject.addProperty("tel", store.getTel());
		jsonObject.add("storePropery", context.serialize(store.getStoreProperty()));
		jsonObject.addProperty("channel", store.getChannel()); 
		jsonObject.addProperty("coverageType", store.getCoverageType()); 
		jsonObject.addProperty("latitude", store.getLatitude());
		jsonObject.addProperty("longitude", store.getLongitude());
		
		return jsonObject;
	}
	
}
