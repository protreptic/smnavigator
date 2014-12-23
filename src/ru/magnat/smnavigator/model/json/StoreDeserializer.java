package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Store;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class StoreDeserializer implements JsonDeserializer<Store> {

	@Override
	public Store deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Store store = new Store();
		store.setId(jsonElement.getAsInt());
		
		return store;
	}

}
