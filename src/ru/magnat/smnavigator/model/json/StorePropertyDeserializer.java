package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.StoreProperty;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class StorePropertyDeserializer implements JsonDeserializer<StoreProperty> {

	@Override
	public StoreProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		StoreProperty storeProperty = new StoreProperty();
		storeProperty.setId(jsonElement.getAsInt());

		return storeProperty;
	}

}
