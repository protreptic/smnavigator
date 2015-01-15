package ru.magnat.smnavigator.fragments;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.synchronization.SynchronizationListener;
import ru.magnat.smnavigator.synchronization.SynchronizationStatus;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment implements SynchronizationListener {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_fragment, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).registerSyncListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		((MainActivity) getActivity()).unregisterSyncListener(this);
	}
	
	@Override
	public void onSynchronizationCompleted(SynchronizationStatus status) {
		switch (status) {
			case STARTED: {
			} break;
			case ACK: {
			
			} break;
			case COMPLETED: {
				
			} break;
			case CANCELED: {
				
			} break;
			case ERROR: {
				
			} break;
			default: {} break;
		}
	}

	@Override
	public void onInitialSynchronizationCompleted(SynchronizationStatus status) {}
	
}
