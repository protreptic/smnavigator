package ru.magnat.smnavigator.fragments;

import android.accounts.Account;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class LocationFragment extends SupportMapFragment implements OnMapReadyCallback {
	
	private Account mAccount;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle arguments = getArguments();
		
		mAccount = arguments.getParcelable("account");
		
		getMapAsync(this); 
	}

	@Override
	public void onMapReady(GoogleMap arg0) {
		
	}
	
}
