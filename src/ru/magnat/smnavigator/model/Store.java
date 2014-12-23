package ru.magnat.smnavigator.model;

import ru.magnat.smnavigator.entities.Clusterable;
import ru.magnat.smnavigator.entities.Jsonable;
import ru.magnat.smnavigator.entities.Mappable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store")
public class Store implements Mappable, Jsonable, Clusterable {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;
	
	@DatabaseField(columnName = "customer", foreign = true, foreignAutoRefresh = true)
	private Customer customer;
	
	@DatabaseField(columnName = "address")
	private String address;
	
	@DatabaseField(columnName = "tel")
	private String tel;
	
	@DatabaseField(columnName = "channel")
	private String channel;
	
	@DatabaseField(columnName = "coverage_type")
	private String coverageType;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
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
	public String toJsonString() {
		Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
		
		return gson.toJson(this);
	}

	@Override
	public LatLng getPosition() {
		return new LatLng(latitude, longitude); 
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", customer=" + customer + ", address=" + address + ", tel=" + tel + ", channel=" + channel + ", coverageType=" + coverageType + ", latitude=" + latitude + ", longitude=" + longitude + "]";	
	}

}
