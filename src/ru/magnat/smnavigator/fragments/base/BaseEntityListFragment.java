package ru.magnat.smnavigator.fragments.base;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.synchronization.util.SynchronizationObserver;
import android.accounts.Account;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseEntityListFragment extends Fragment implements SynchronizationObserver {
	
	protected Account mAccount;
	protected RecyclerView mRecyclerView;
	protected RecyclerView.Adapter<?> mRecyclerViewAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setReverseLayout(false);
		
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).registerSynchronizationObserver(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		((MainActivity) getActivity()).unregisterSynchronizationObserver(this);
	}
	
	@Override
	public void onStarted() {
	}

	@Override
	public void onAck() {
	}

	@Override
	public void onCompleted() {
	}

	@Override
	public void onCanceled() {
	}

	@Override
	public void onError() {
	}
	
}
