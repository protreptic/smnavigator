package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Customer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomerSerializer implements JsonSerializer<Customer> {

	@Override
	public JsonElement serialize(Customer customer, Type type, JsonSerializationContext context) {
		return null;
	}
	
}
