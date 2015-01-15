package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.experimental.TrackFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoreFragment extends Fragment {

	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mTabHost = (FragmentTabHost) inflater.inflate(R.layout.store_fragment, container, false);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		
		mTabHost.addTab(mTabHost.newTabSpec("simple1").setIndicator("Маршруты"), TrackFragment.class, getArguments());
		mTabHost.addTab(mTabHost.newTabSpec("simple2").setIndicator("Маршруты движения"), RouteFragment.class, getArguments());
		
		return mTabHost;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mTabHost = null;
	}

}
