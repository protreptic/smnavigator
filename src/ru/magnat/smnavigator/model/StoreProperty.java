package ru.magnat.smnavigator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store_property")
public class StoreProperty {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "golden_status")
	private String goldenStatus;
	
	@DatabaseField(columnName = "is_potential")
	private Boolean isPotential;
	
	@DatabaseField(columnName = "is_active")
	private Boolean isActive;
	
	@DatabaseField(columnName = "is_visited")
	private Boolean isVisited;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id="+ id + ", goldenStatus=" + goldenStatus + ", isPotential=" + isPotential + ", isActive=" + isActive + ", isVisited=" + isVisited + "]";	
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGoldenStatus() {
		return goldenStatus;
	}

	public void setGoldenStatus(String goldenStatus) {
		this.goldenStatus = goldenStatus;
	}

	public Boolean getIsPotential() {
		return isPotential;
	}

	public void setIsPotential(Boolean isPotential) {
		this.isPotential = isPotential;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVisited() {
		return isVisited;
	}

	public void setIsVisited(Boolean isVisited) {
		this.isVisited = isVisited;
	}

}
