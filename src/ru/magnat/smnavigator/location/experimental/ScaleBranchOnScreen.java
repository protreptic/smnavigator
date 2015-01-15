package ru.magnat.smnavigator.location.experimental;

import java.util.List;

import ru.magnat.smnavigator.model.Location;
import ru.magnat.smnavigator.model.experimental.Geocoordinate;

public class ScaleBranchOnScreen {

	private List<Geocoordinate> geocoordinates;
	
	public ScaleBranchOnScreen(List<Geocoordinate> geocoordinates) {
		this.geocoordinates = geocoordinates;
	}
	
	private Location findMaxX() { 
		Location location = null;
		
		Integer max = geocoordinates.get(0).getId();
		
		for (Geocoordinate geocoordinate : geocoordinates) {
			double latitude = geocoordinate.getLatitude();
			double longitude = geocoordinate.getLongitude();
			
			
		}
		
		return location; 
	}
	
	private Location findMinX() {
		Location location = null;
		
		return location; 
	}
	
	private Location findMaxY() {
		Location location = null;
		
		return location; 
	}
	
	private Location findMinY() {
		Location location = null;
		
		return location; 
	}
	
}
