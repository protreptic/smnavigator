package ru.magnat.smnavigator.util;

import com.google.android.maps.GeoPoint;

public class Location {
	
	public static GeoPoint valueOf(double latitude, double longitude) {
		return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}
	
	public static GeoPoint valueOf(android.location.Location location) {
		// Getting latitude
		double latitude = location.getLatitude();

		// Getting longitude
		double longitude = location.getLongitude();
		
		return new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
	}
	
}
