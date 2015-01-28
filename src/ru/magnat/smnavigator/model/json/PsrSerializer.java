package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Psr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PsrSerializer implements JsonSerializer<Psr> {

	@Override
	public JsonElement serialize(Psr psr, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("id", context.serialize(psr.getId()));
		jsonObject.addProperty("name", psr.getName());
		jsonObject.addProperty("project", psr.getProject());
		jsonObject.addProperty("email", psr.getEmail());
		jsonObject.addProperty("tel", psr.getTel());
		jsonObject.add("branch", context.serialize(psr.getBranch()));
		jsonObject.add("department", context.serialize(psr.getDepartment())); 
		jsonObject.addProperty("latitude", psr.getLatitude());
		jsonObject.addProperty("longitude", psr.getLongitude());
		
		return jsonObject;
	}
	
}
