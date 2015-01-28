package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Department;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DepartmentSerializer implements JsonSerializer<Department> {

	@Override
	public JsonElement serialize(Department department, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", department.getId());
		jsonObject.addProperty("name", department.getName());
		
		return jsonObject;
	}
	
}
