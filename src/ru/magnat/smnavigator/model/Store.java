package ru.magnat.smnavigator.model;

import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.model.base.Clusterable;
import ru.magnat.smnavigator.model.base.Mappable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store")
public class Store implements Parcelable, Mappable, Clusterable {
	
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
	
	@DatabaseField(columnName = "store_property", foreign = true, foreignAutoRefresh = true)
	private StoreProperty storeProperty;
	
	@DatabaseField(columnName = "channel")
	private String channel;
	
	@DatabaseField(columnName = "coverage_type")
	private String coverageType;
	
	@DatabaseField(columnName = "latitude")
	private Double latitude;
	
	@DatabaseField(columnName = "longitude")
	private Double longitude;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<Target> targets;
	
	public Store() {}
	
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
		return Text.prepareAddress(address);
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
	public LatLng getPosition() {
		return new LatLng(latitude, longitude); 
	}
	
	public StoreProperty getStoreProperty() {
		return storeProperty;
	}

	public void setStoreProperty(StoreProperty storeProperty) {
		this.storeProperty = storeProperty;
	}

	public ForeignCollection<Target> getTargets() {
		return targets;
	}

	public void setTargets(ForeignCollection<Target> targets) {
		this.targets = targets;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", customer=" + customer + ", address=" + address + ", tel=" + tel + ", channel=" + channel + ", coverageType=" + coverageType + ", latitude=" + latitude + ", longitude=" + longitude + "]";	
	}
	
	private Store(Parcel parcel) {
		setId(parcel.readInt()); 
		setName(parcel.readString()); 
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId()); 
		parcel.writeString(getName());
	} 

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {
		public Store createFromParcel(Parcel parcel) {
			return new Store(parcel);
		}

		public Store[] newArray(int size) {
			return new Store[size];
		}
	};

}
