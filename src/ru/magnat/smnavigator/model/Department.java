package ru.magnat.smnavigator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "department")
public class Department implements Parcelable {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "name")
	private String name;

	public Department() {}
	
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", name=" + name + "]";
	}
	
	private Department(Parcel parcel) {
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
	
	public static final Parcelable.Creator<Department> CREATOR = new Parcelable.Creator<Department>() {
		public Department createFromParcel(Parcel parcel) {
			return new Department(parcel);
		}

		public Department[] newArray(int size) {
			return new Department[size];
		}
	};
	
}
