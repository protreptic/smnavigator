package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.model.parcel.StoreParcel;
import ru.magnat.smnavigator.sync.SyncStatus;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TargetFragment extends DefaultEntityListFragment<Target> {
	
	@Override
	protected Adapter<?> createAdapter() {
		return new RecyclerView.Adapter<TargetViewHolder>() {
			
			@Override
			public int getItemCount() {
				return mEntityList.size();
			}

			@Override
			public void onBindViewHolder(TargetViewHolder holder, int position) {
				Target target = mEntityList.get(position);
				
				holder.kpiName.setTypeface(mRobotoCondensedBold);  
				holder.kpiName.setText(target.getName());
				
				holder.targetView.setTypeface(mRobotoCondensedBold);  
				holder.targetView.setText(String.format("%.2f", target.getTarget()));
				
				holder.fact.setTypeface(mRobotoCondensedBold);  
				holder.fact.setText(String.format("%.2f", target.getFact()));
				
				holder.index.setTypeface(mRobotoCondensedBold);  
				holder.index.setText(String.format("%.2f%%", target.getIndex()));
			}

			@Override
			public TargetViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
				View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_layout, parent, false);
				
				return new TargetViewHolder(itemView);
			}
			
		};
	} 
	
	@Override
	protected void refreshData() {
		new SecuredStorageDataLoader() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					StoreParcel storeParcel = getArguments().getParcelable("store");
					
					mQueryBuilder = mSecuredStorage.getTargetDao().queryBuilder();
					mQueryBuilder.where()
						.like("name", mQueryText)
							.and()
						.eq("store", storeParcel.getStore().getId());
					mQueryBuilder.orderBy("name", true);
					
					mEntityList.addAll(mQueryBuilder.query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute();
	}
	
	public static class TargetViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView kpiName;
		protected TextView targetView;
		protected TextView fact;
		protected TextView index;
		
		public TargetViewHolder(View itemView) {
			super(itemView);
			
			kpiName = (TextView) itemView.findViewById(R.id.kpiName); 
			targetView = (TextView) itemView.findViewById(R.id.target); 
			fact = (TextView) itemView.findViewById(R.id.fact); 
			index = (TextView) itemView.findViewById(R.id.index); 
		}
		
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
