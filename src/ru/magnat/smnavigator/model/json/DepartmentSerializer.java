package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Department;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DepartmentSerializer implements JsonSerializer<Department> {

	@Override
	public JsonElement serialize(Department department, Type type, JsonSerializationContext context) {
		return null;
	}
	
}
