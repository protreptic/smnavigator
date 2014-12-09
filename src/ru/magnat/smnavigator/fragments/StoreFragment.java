package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.view.StoreView;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoreFragment extends DialogFragment {
	
	private Store store;
	
	public StoreFragment(Store store) {
		this.store = store;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return new StoreView(getActivity(), store);
	}
	
}
