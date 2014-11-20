package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationCompositeFragment extends Fragment {
	
	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_composite_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
		mTabHost.setup(getActivity(), getActivity().getSupportFragmentManager(), android.R.id.tabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("map").setIndicator("Карта"), NavigationMapFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("objects").setIndicator("Объекты"), NavigationListFragment.class, null);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mTabHost = null;
	}
	
}
