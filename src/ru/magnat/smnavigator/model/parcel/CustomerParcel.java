package ru.magnat.smnavigator.model.parcel;

import ru.magnat.smnavigator.model.Customer;
import android.os.Parcel;
import android.os.Parcelable;

public class CustomerParcel implements Parcelable {
	
	private Customer customer;
	
	public CustomerParcel(Customer customer) {
		this.customer = customer;
	}
	
	private CustomerParcel(Parcel parcel) {
		customer.setId(parcel.readInt()); 
		customer.setName(parcel.readString()); 
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(customer.getId()); 
		parcel.writeString(customer.getName());
	} 

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<CustomerParcel> CREATOR = new Parcelable.Creator<CustomerParcel>() {
		public CustomerParcel createFromParcel(Parcel parcel) {
			return new CustomerParcel(parcel);
		}

		public CustomerParcel[] newArray(int size) {
			return new CustomerParcel[size];
		}
	};
	
}
