package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Store;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StoreSerializer implements JsonSerializer<Store> {

	@Override
	public JsonElement serialize(Store store, Type type, JsonSerializationContext context) {
		return null;
	}
	
}
