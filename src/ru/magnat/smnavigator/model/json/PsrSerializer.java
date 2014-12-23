package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Psr;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PsrSerializer implements JsonSerializer<Psr> {

	@Override
	public JsonElement serialize(Psr psr, Type type, JsonSerializationContext context) {
		return null;
	}
	
}
