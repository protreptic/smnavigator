package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.StoreProperty;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StorePropertySerializer implements JsonSerializer<StoreProperty> {

	@Override
	public JsonElement serialize(StoreProperty storeProperties, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", storeProperties.getId());
		jsonObject.addProperty("goldenStatus", storeProperties.getGoldenStatus());
		
		return jsonObject;
	}
	
}
