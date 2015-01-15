package ru.magnat.smnavigator.model.experimental.parcel;

import ru.magnat.smnavigator.model.Target;
import android.os.Parcel;
import android.os.Parcelable;

public class TargetParcel implements Parcelable {
	
	private Target target;
	
	public TargetParcel(Target target) {
		this.target = target;
	}
	
	private TargetParcel(Parcel parcel) {
		target.setId(parcel.readInt()); 
		
		StoreParcel storeParcel = parcel.readParcelable(null);
		
		target.setStore(storeParcel.getStore()); 
		target.setName(parcel.readString()); 
		
		target.setTarget(parcel.readDouble()); 
		target.setFact(parcel.readDouble()); 
		target.setIndex(parcel.readDouble()); 
	}
	
	public Target getTarget() {
		return target;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(target.getId()); 
		
		StoreParcel storeParcel = new StoreParcel(target.getStore());
		
		parcel.writeParcelable(storeParcel, 0);
		parcel.writeString(target.getName());
		parcel.writeDouble(target.getTarget()); 
		parcel.writeDouble(target.getFact());
		parcel.writeDouble(target.getIndex());
	} 
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<TargetParcel> CREATOR = new Parcelable.Creator<TargetParcel>() {
		public TargetParcel createFromParcel(Parcel in) {
			return new TargetParcel(in);
		}

		public TargetParcel[] newArray(int size) {
			return new TargetParcel[size];
		}
	};
	
}
