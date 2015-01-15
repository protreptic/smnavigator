package ru.magnat.smnavigator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "target")
public class Target implements Parcelable {
	
	@DatabaseField(columnName = "id", generatedId = true)
	private Integer id;
	
	@DatabaseField(columnName = "store", foreign = true, foreignAutoRefresh = true)
	private Store store;
	
	@DatabaseField(columnName = "name")
	private String name;
	
	@DatabaseField(columnName = "target")
	private Double target;
	
	@DatabaseField(columnName = "fact")
	private Double fact;
	
	@DatabaseField(columnName = "index")
	private Double index;
	
	public Target() {}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}

	public Double getFact() {
		return fact;
	}

	public void setFact(Double fact) {
		this.fact = fact;
	}

	public Double getIndex() {
		return index;
	}

	public void setIndex(Double index) {
		this.index = index;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id="+ id + ", store=" + store + ", name=" + name + ", target=" + target + ", fact=" + fact + ", index=" + index + "]";	
	}
	
	private Target(Parcel parcel) {
		setId(parcel.readInt()); 
		//setStore(parcel.readParcelable(Store.class)); 
		setName(parcel.readString());  
		setTarget(parcel.readDouble()); 
		setFact(parcel.readDouble()); 
		setIndex(parcel.readDouble()); 
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId()); 
		parcel.writeParcelable(getStore(), 0);
		parcel.writeString(getName());
		parcel.writeDouble(getTarget()); 
		parcel.writeDouble(getFact());
		parcel.writeDouble(getIndex());
	} 
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Target> CREATOR = new Parcelable.Creator<Target>() {
		public Target createFromParcel(Parcel parcel) {
			return new Target(parcel);
		}

		public Target[] newArray(int size) {
			return new Target[size];
		}
	};

}
