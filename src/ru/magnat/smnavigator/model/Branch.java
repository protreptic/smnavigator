package ru.magnat.smnavigator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "branch")
public class Branch {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;

	@DatabaseField(columnName = "location", foreign = true, foreignAutoRefresh = true)
	private Location location;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", location=" + location + "]";
	}
	
}
