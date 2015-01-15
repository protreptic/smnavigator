package ru.magnat.smnavigator.model;

import java.sql.Date;

import ru.magnat.smnavigator.model.experimental.Geocoordinate;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "track")
public class Track {
	
	@DatabaseField(columnName = "id", generatedId = true)
	private Integer id;
	
	@DatabaseField(columnName = "track_date")
	private Date trackDate;
	
	@DatabaseField(columnName = "manager", foreign = true, foreignAutoRefresh = true)
	private Manager manager;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<Geocoordinate> geocoordinates;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTrackDate() {
		return trackDate;
	}

	public void setTrackDate(Date trackDate) {
		this.trackDate = trackDate;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public ForeignCollection<Geocoordinate> getGeocoordinates() {
		return geocoordinates;
	}

	public void setGeocoordinates(ForeignCollection<Geocoordinate> geocoordinates) {
		this.geocoordinates = geocoordinates;
	}
	
}
