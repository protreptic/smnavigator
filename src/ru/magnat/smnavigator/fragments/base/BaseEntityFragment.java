package ru.magnat.smnavigator.fragments.base;

import ru.magnat.smnavigator.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseEntityFragment extends Fragment {

	protected FragmentTabHost mFragmentTabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentTabHost = (FragmentTabHost) inflater.inflate(R.layout.entity_fragment, container, false);
		mFragmentTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		
		return mFragmentTabHost;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mFragmentTabHost = null;
	}
	
}
