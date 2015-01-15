package ru.magnat.smnavigator.model;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "route")
public class Route {

	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "visit_date")
	private Date visitDate;
	
	@DatabaseField(columnName = "psr", foreign = true, foreignAutoRefresh = true)
	private Psr psr;
	
	@DatabaseField(columnName = "store", foreign = true, foreignAutoRefresh = true)
	private Store store;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Psr getPsr() {
		return psr;
	}

	public void setPsr(Psr psr) {
		this.psr = psr;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", visitDate=" + visitDate + ", psr=" + psr + ", store=" + store + "]";
	}
	
}
