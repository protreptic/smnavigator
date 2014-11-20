package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.maps.MapView;

public class NavigationMapFragment extends Fragment {

	private MapView mMapView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_map_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMapView = new MapView(getActivity(), "0n7IqmcMRCnDYfxEdmJWdykrbYwOBFa2xylOxIw");
		mMapView.setClickable(true);

		mMapView.getController().setZoom(15);
		mMapView.setBuiltInZoomControls(true);
		
		RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.map_view);
		relativeLayout.addView(mMapView); 
	}
	
}
