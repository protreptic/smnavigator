package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Psr;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PsrDeserializer implements JsonDeserializer<Psr> {

	@Override
	public Psr deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Psr psr = new Psr();
		psr.setId(jsonElement.getAsInt());
		
		return psr;
	}

}
