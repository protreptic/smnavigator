package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Branch;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BranchSerializer implements JsonSerializer<Branch> {

	@Override
	public JsonElement serialize(Branch branch, Type type, JsonSerializationContext context) {
		return null;
	}
	
}
