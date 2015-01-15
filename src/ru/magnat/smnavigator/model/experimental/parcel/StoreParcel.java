package ru.magnat.smnavigator.model.experimental.parcel;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;

import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Target;
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
		
		CustomerParcel customerParcel = parcel.readParcelable(null);
		
		store.setCustomer(customerParcel.getCustomer()); 
		store.setAddress(parcel.readString());
		store.setTel(parcel.readString());
		
		StorePropertyParcel storePropertyParcel = parcel.readParcelable(null);
		
		store.setStoreProperty(storePropertyParcel.getStoreProperty()); 
		store.setChannel(parcel.readString());
		store.setCoverageType(parcel.readString());
		store.setLatitude(parcel.readDouble());
		store.setLongitude(parcel.readDouble()); 
		
		List<TargetParcel> targetParcels = new ArrayList<TargetParcel>();
		
		parcel.readTypedList(targetParcels, TargetParcel.CREATOR); 
		
		List<Target> targets = new ArrayList<Target>();
		
		for (TargetParcel targetParcel : targetParcels) {
			targets.add(targetParcel.getTarget());
		}
		
		store.setTargets((ForeignCollection<Target>) targets);
	} 
	
	public Store getStore() {
		return store;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(store.getId()); 
		parcel.writeString(store.getName());
		
		CustomerParcel customerParcel = new CustomerParcel(store.getCustomer());
		
		parcel.writeParcelable(customerParcel, 0);
		parcel.writeString(store.getAddress());
		parcel.writeString(store.getTel());
		
		StorePropertyParcel storePropertyParcel = new StorePropertyParcel(store.getStoreProperty());
		
		parcel.writeParcelable(storePropertyParcel, 0);
		parcel.writeString(store.getChannel());
		parcel.writeString(store.getCoverageType()); 
		parcel.writeDouble(store.getLatitude());
		parcel.writeDouble(store.getLongitude());
		
		ArrayList<TargetParcel> targets = parcel.createTypedArrayList(TargetParcel.CREATOR);
		
		for (Target target : store.getTargets()) {
			targets.add(new TargetParcel(target));
		}
		
		parcel.writeTypedList(targets); 
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
