package ru.magnat.smnavigator.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "route")
public class Route {

	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "visit_date")
	private String visitDate;
	
	@DatabaseField(columnName = "psr")
	private Integer psr;
	
	@DatabaseField(columnName = "store")
	private Integer store;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getPsr() {
		return psr;
	}

	public void setPsr(Integer psr) {
		this.psr = psr;
	}

	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " [id=" + id + ", visitDate=" + visitDate + ", psr=" + psr + ", store=" + store + "]";
	}
	
}
