package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class LocationDeserializer implements JsonDeserializer<Location> {

	@Override
	public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Location location = new Location();
		location.setId(jsonElement.getAsInt());
		
		return location;
	}

}
