package ru.magnat.smnavigator.model;

import ru.magnat.smnavigator.model.base.Mappable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "georegion")
public class Georegion implements Mappable {

	@DatabaseField(columnName = "id", generatedId = true)
	private Integer id;
	
	@DatabaseField(columnName = "branch_id")
	private Integer branchId;
	
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

	public Integer getBranchId() {
		return branchId;
	}

	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
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
		return getClass().getSimpleName() + " [id=" + id + ", branchId=" + branchId + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
