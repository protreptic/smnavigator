package ru.magnat.smnavigator.model.experimental.parcel;

import ru.magnat.smnavigator.model.Store;
import android.os.Parcel;
import android.os.Parcelable;

public class StoreParcel implements Parcelable {

	private Store store;
	
	public StoreParcel(Store store) {
		this.store = store;
	}
	
	private StoreParcel(Parcel parcel) {
		store.setId(parcel.readInt()); 
		store.setName(parcel.readString()); 
		store.setAddress(parcel.readString());
		store.setTel(parcel.readString());
		store.setChannel(parcel.readString());
		store.setCoverageType(parcel.readString());
		store.setLatitude(parcel.readDouble());
		store.setLongitude(parcel.readDouble()); 
	} 
	
	public Store getStore() {
		return store;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(store.getId()); 
		parcel.writeString(store.getName());
		parcel.writeString(store.getAddress());
		parcel.writeString(store.getTel());
		parcel.writeString(store.getChannel());
		parcel.writeString(store.getCoverageType()); 
		parcel.writeDouble(store.getLatitude());
		parcel.writeDouble(store.getLongitude());
	} 

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<StoreParcel> CREATOR = new Parcelable.Creator<StoreParcel>() {
		public StoreParcel createFromParcel(Parcel in) {
			return new StoreParcel(in);
		}

		public StoreParcel[] newArray(int size) {
			return new StoreParcel[size];
		}
	};

}
