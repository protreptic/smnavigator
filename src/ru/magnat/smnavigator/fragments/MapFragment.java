package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.map.LocationHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {
	
	private LocationHelper mLocationHelper;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		GoogleMap map = getMap();
		map.setMyLocationEnabled(true);
		
		mLocationHelper = LocationHelper.getInstance(getActivity(), map);
		mLocationHelper.updateOverlays();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_fragment_menu, menu);
		
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}
	
}
