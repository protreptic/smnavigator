package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.sync.SyncObserver;
import ru.magnat.smnavigator.sync.SyncStatus;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;

public class PsrListFragment extends DefaultEntityListFragment<Psr> implements SyncObserver {

	@Override
	protected Adapter<?> createAdapter() {
		return new DefaultAdapter() {
			
			@Override
			public void onBindViewHolder(DefaultViewHolder holder, int position) {
				final Psr psr = mEntityList.get(position);
				
				holder.title.setText(psr.getName());
				holder.title.setTypeface(mRobotoCondensedBold);
				
				holder.subtitle.setText(psr.getBranch().getName());
				holder.subtitle.setTypeface(mRobotoCondensedLight);
				
				holder.description.setText(psr.getDepartment().getName()); 
				holder.description.setTypeface(mRobotoCondensedLight);
				
				holder.staticmaptitle.setText(psr.getProject());
				holder.staticmaptitle.setTypeface(mRobotoCondensedLight);
				
				holder.itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						Bundle arguments = new Bundle();
						arguments.putParcelable("account", mAccount);
						arguments.putInt("psr_id", psr.getId());   
						
						Fragment fragment = new PsrFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				holder.staticmap.setGeocoordinate(psr.getLatitude(), psr.getLongitude());
			}
		};
	} 
	
	@Override
	protected void refreshData() {
		new SecuredStorageDataLoader() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					mQueryBuilder = mSecuredStorage.getPsrDao().queryBuilder();
					mQueryBuilder.where()
						.like("name", mQueryText)
							.or()
						.like("project", mQueryText);
					mQueryBuilder.orderBy("name", true);
					
					mEntityList.addAll(mQueryBuilder.query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).registerSyncObserver(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		((MainActivity) getActivity()).unregisterSyncObserver(this);
	}
	
	@Override
	public void onStatusChanged(SyncStatus status) {
		switch (status) {
			case STARTED: {
				
			} break;
			case ACK: {
				
			} break;
			case COMPLETED: {
				refreshData();
			} break;
			case CANCELED: {
				
			} break;
			case ERROR: {
				
			} break;
			default: {
				throw new RuntimeException();
			}
		}
	}
	
}
