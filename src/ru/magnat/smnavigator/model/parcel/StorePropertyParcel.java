package ru.magnat.smnavigator.model.parcel;

import ru.magnat.smnavigator.model.StoreProperty;
import android.os.Parcel;
import android.os.Parcelable;

public class StorePropertyParcel implements Parcelable {

	private StoreProperty storeProperty;
	
	public StorePropertyParcel(StoreProperty storeProperty) {
		this.storeProperty = storeProperty;
	}
	
	private StorePropertyParcel(Parcel parcel) {
		storeProperty.setId(parcel.readInt()); 
		storeProperty.setGoldenStatus(parcel.readString()); 
	}
	
	public StoreProperty getStoreProperty() {
		return storeProperty;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(storeProperty.getId()); 
		parcel.writeString(storeProperty.getGoldenStatus());
	} 

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<StorePropertyParcel> CREATOR = new Parcelable.Creator<StorePropertyParcel>() {
		public StorePropertyParcel createFromParcel(Parcel parcel) {
			return new StorePropertyParcel(parcel);
		}

		public StorePropertyParcel[] newArray(int size) {
			return new StorePropertyParcel[size];
		}
	};

}
