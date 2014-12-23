package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Department;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DepartmentDeserializer implements JsonDeserializer<Department> {

	@Override
	public Department deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Department department = new Department();
		department.setId(jsonElement.getAsInt());
		
		return department;
	}

}
