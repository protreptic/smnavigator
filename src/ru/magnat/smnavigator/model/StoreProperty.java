package ru.magnat.smnavigator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store_property")
public class StoreProperty {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "golden_status")
	private String goldenStatus;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id="+ id + ", goldenStatus=" + goldenStatus + "]";	
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

}
