package ru.magnat.smnavigator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "branch")
public class Branch implements Parcelable {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;

	@DatabaseField(columnName = "location", foreign = true, foreignAutoRefresh = true)
	private Location location;
	
	public Branch() {}
	
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
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", location=" + location + "]";
	}
	
	private Branch(Parcel parcel) {
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
	
	public static final Parcelable.Creator<Branch> CREATOR = new Parcelable.Creator<Branch>() {
		public Branch createFromParcel(Parcel parcel) {
			return new Branch(parcel);
		}

		public Branch[] newArray(int size) {
			return new Branch[size];
		}
	};
	
}
