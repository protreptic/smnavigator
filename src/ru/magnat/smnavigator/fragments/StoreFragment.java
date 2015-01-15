package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoreFragment extends BaseEntityFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("targets").setIndicator(getString(R.string.targets)), TargetFragment.class, getArguments());

		return mFragmentTabHost;
	}

}
