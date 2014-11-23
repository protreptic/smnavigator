package ru.magnat.smnavigator.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store")
public class Store {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;
	
	@DatabaseField(columnName = "customer")
	private String customer;
	
	@DatabaseField(columnName = "address")
	private String address;
	
	@DatabaseField(columnName = "tel")
	private String tel;
	
	@DatabaseField(columnName = "channel")
	private String channel;
	
	@DatabaseField(columnName = "coverage_type")
	private String coverageType;
	
	@DatabaseField(columnName = "golden_status")
	private String goldenStatus;
	
	@DatabaseField(columnName = "psr", foreign = true)
	private Psr psr;
	
	@DatabaseField(columnName = "latitude")
	private Double latitude;
	
	@DatabaseField(columnName = "longitude")
	private Double longitude;

	@DatabaseField(columnName = "store_statistics", foreign = true)
	private StoreStatistics storeStatistics;
	
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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(String coverageType) {
		this.coverageType = coverageType;
	}

	public String getGoldenStatus() {
		return goldenStatus;
	}

	public void setGoldenStatus(String goldenStatus) {
		this.goldenStatus = goldenStatus;
	}

	public Psr getPsr() {
		return psr;
	}

	public void setPsr(Psr psr) {
		this.psr = psr;
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

	public StoreStatistics getStoreStatistics() {
		return storeStatistics;
	}

	public void setStoreStatistics(StoreStatistics storeStatistics) {
		this.storeStatistics = storeStatistics;
	}
	
}
