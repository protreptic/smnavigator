package ru.magnat.smnavigator.model;

import java.sql.Timestamp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "measure")
public class Measure {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "visit_frequency")
	private Integer frequencyOfVisits;
	
	@DatabaseField(columnName = "last_visit")
	private Timestamp lastVisit;
	
	@DatabaseField(columnName = "next_visit")
	private Timestamp nextVisit;
	
	@DatabaseField(columnName = "turnover_previous_month")
	private Double turnoverPreviousMonth;
	
	@DatabaseField(columnName = "turnover_current_month")
	private Double turnoverCurrentMonth;
	
	@DatabaseField(columnName = "total_distribution")
	private Integer totalDistribution;
	
	@DatabaseField(columnName = "golden_distribution")
	private Integer goldenDistribution;

	@DatabaseField(columnName = "golden_status")
	private String goldenStatus;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Timestamp lastVisit) {
		this.lastVisit = lastVisit;
	}

	public Timestamp getNextVisit() {
		return nextVisit;
	}

	public void setNextVisit(Timestamp nextVisit) {
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

	public Integer getTotalDistribution() {
		return totalDistribution;
	}

	public void setTotalDistribution(Integer totalDistribution) {
		this.totalDistribution = totalDistribution;
	}

	public Integer getGoldenDistribution() {
		return goldenDistribution;
	}

	public void setGoldenDistribution(Integer goldenDistribution) {
		this.goldenDistribution = goldenDistribution;
	}
	
	public String getGoldenStatus() {
		return goldenStatus;
	}

	public void setGoldenStatus(String goldenStatus) {
		this.goldenStatus = goldenStatus;
	}

	public Integer getFrequencyOfVisits() {
		return frequencyOfVisits;
	}

	public void setFrequencyOfVisits(Integer frequencyOfVisits) {
		this.frequencyOfVisits = frequencyOfVisits;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", frequencyOfVisits=" + frequencyOfVisits + ", lastVisit=" + lastVisit + ", nextVisit=" + nextVisit + ", turnoverPreviousMonth=" + turnoverPreviousMonth + ", turnoverCurrentMonth=" + turnoverCurrentMonth + ", totalDistribution=" + totalDistribution + ", goldenDistribution=" + goldenDistribution + ", goldenStatus=" + goldenStatus + "]";	
	}
	
}
