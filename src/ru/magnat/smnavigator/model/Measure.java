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

	public Integer getFrequencyOfVisits() {
		return frequencyOfVisits;
	}

	public void setFrequencyOfVisits(Integer frequencyOfVisits) {
		this.frequencyOfVisits = frequencyOfVisits;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", frequencyOfVisits=" + frequencyOfVisits + ", lastVisit=" + lastVisit + ", nextVisit=" + nextVisit + "]";	
	}
	
}
