package ru.magnat.smnavigator.model.experimental;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "geocoordinate")
public class Geocoordinate {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "latitude")
	private Double latitude;
	
	@DatabaseField(columnName = "longitude")
	private Double longitude;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
