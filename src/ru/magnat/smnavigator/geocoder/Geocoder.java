package ru.magnat.smnavigator.geocoder;

import ru.magnat.smnavigator.entities.Mappable;

public interface Geocoder {

	public Mappable geocode(String query);
	
}
