package ru.magnat.smnavigator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "store_property")
public class StoreProperty implements Parcelable {
	
	@DatabaseField(columnName = "id", id = true)
	private Integer id;
	
	@DatabaseField(columnName = "golden_status")
	private String goldenStatus;
	
	public StoreProperty() {}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGoldenStatus() {
		return goldenStatus;
	}

	public void setGoldenStatus(String goldenStatus) {
		this.goldenStatus = goldenStatus;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id="+ id + ", goldenStatus=" + goldenStatus + "]";	
	}
	
	private StoreProperty(Parcel parcel) {
		setId(parcel.readInt()); 
		setGoldenStatus(parcel.readString()); 
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId()); 
		parcel.writeString(getGoldenStatus());
	} 

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<StoreProperty> CREATOR = new Parcelable.Creator<StoreProperty>() {
		public StoreProperty createFromParcel(Parcel parcel) {
			return new StoreProperty(parcel);
		}

		public StoreProperty[] newArray(int size) {
			return new StoreProperty[size];
		}
	};

}
