package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Branch;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BranchDeserializer implements JsonDeserializer<Branch> {

	@Override
	public Branch deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Branch branch = new Branch();
		branch.setId(jsonElement.getAsInt());
		
		return branch;
	}

}
