package ru.magnat.smnavigator.model.json;

import java.lang.reflect.Type;

import ru.magnat.smnavigator.model.Customer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CustomerDeserializer implements JsonDeserializer<Customer> {

	@Override
	public Customer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		Customer customer = new Customer();
		customer.setId(jsonElement.getAsInt());
		
		return customer;
	}

}
