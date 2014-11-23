package ru.magnat.smnavigator.entities;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store_staistics")
public class StoreStatistics {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "store")
	private Integer store;
	
	@DatabaseField(columnName = "last_visit")
	private Date lastVisit;
	
	@DatabaseField(columnName = "next_visit")
	private Date nextVisit;
	
	@DatabaseField(columnName = "turnover_previous_month")
	private Float turnoverPreviousMonth;
	
	@DatabaseField(columnName = "turnover_current_month")
	private Float turnoverCurrentMonth;
	
	@DatabaseField(columnName = "total_distribution")
	private Float totalDistribution;
	
	@DatabaseField(columnName = "golden_distribution")
	private Float goldenDistribution;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}

	public Date getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	public Date getNextVisit() {
		return nextVisit;
	}

	public void setNextVisit(Date nextVisit) {
		this.nextVisit = nextVisit;
	}

	public Float getTurnoverPreviousMonth() {
		return turnoverPreviousMonth;
	}

	public void setTurnoverPreviousMonth(Float turnoverPreviousMonth) {
		this.turnoverPreviousMonth = turnoverPreviousMonth;
	}

	public Float getTurnoverCurrentMonth() {
		return turnoverCurrentMonth;
	}

	public void setTurnoverCurrentMonth(Float turnoverCurrentMonth) {
		this.turnoverCurrentMonth = turnoverCurrentMonth;
	}

	public Float getTotalDistribution() {
		return totalDistribution;
	}

	public void setTotalDistribution(Float totalDistribution) {
		this.totalDistribution = totalDistribution;
	}

	public Float getGoldenDistribution() {
		return goldenDistribution;
	}

	public void setGoldenDistribution(Float goldenDistribution) {
		this.goldenDistribution = goldenDistribution;
	}
	
}
