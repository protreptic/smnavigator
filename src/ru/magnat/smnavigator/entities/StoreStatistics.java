package ru.magnat.smnavigator.entities;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store_statistics")
public class StoreStatistics {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "last_visit")
	private Date lastVisit;
	
	@DatabaseField(columnName = "next_visit")
	private Date nextVisit;
	
	@DatabaseField(columnName = "turnover_previous_month")
	private Double turnoverPreviousMonth;
	
	@DatabaseField(columnName = "turnover_current_month")
	private Double turnoverCurrentMonth;
	
	@DatabaseField(columnName = "total_distribution")
	private Double totalDistribution;
	
	@DatabaseField(columnName = "golden_distribution")
	private Double goldenDistribution;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Double getTurnoverPreviousMonth() {
		return turnoverPreviousMonth;
	}

	public void setTurnoverPreviousMonth(Double turnoverPreviousMonth) {
		this.turnoverPreviousMonth = turnoverPreviousMonth;
	}

	public Double getTurnoverCurrentMonth() {
		return turnoverCurrentMonth;
	}

	public void setTurnoverCurrentMonth(Double turnoverCurrentMonth) {
		this.turnoverCurrentMonth = turnoverCurrentMonth;
	}

	public Double getTotalDistribution() {
		return totalDistribution;
	}

	public void setTotalDistribution(Double totalDistribution) {
		this.totalDistribution = totalDistribution;
	}

	public Double getGoldenDistribution() {
		return goldenDistribution;
	}

	public void setGoldenDistribution(Double goldenDistribution) {
		this.goldenDistribution = goldenDistribution;
	}
	
}
