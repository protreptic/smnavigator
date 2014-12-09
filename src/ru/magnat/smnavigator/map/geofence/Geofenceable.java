package ru.magnat.smnavigator.map.geofence;

import ru.magnat.smnavigator.entities.Jsonable;
import ru.magnat.smnavigator.entities.Mappable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "georegion")
public class Geofenceable implements Mappable, Jsonable {

	@DatabaseField(columnName = "id")
	private Integer id;
	
	@DatabaseField(columnName = "latitude")
	private Double latitude;
	
	@DatabaseField(columnName = "longitude")
	private Double longitude;
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toJsonString() {
		Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
		
		return gson.toJson(this);
	}
	
}
