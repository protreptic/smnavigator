package ru.magnat.smnavigator.model.experimental;

import ru.magnat.smnavigator.model.Track;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "geocoordinate")
public class Geocoordinate {
	
	@DatabaseField(columnName = "id", generatedId = true)
	private Integer id;
	
	@DatabaseField(columnName = "track", foreign = true, foreignAutoRefresh = true)
	private Track track;
	
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

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}
	
}
