package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityListFragment;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.parcel.CustomerParcel;
import ru.magnat.smnavigator.model.parcel.StoreParcel;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;

import com.j256.ormlite.stmt.Where;

public class StoreListFragment extends BaseEntityListFragment<Store> {

	@Override
	protected Adapter<?> createAdapter() {
		return new DefaultAdapter() {
			
			@Override
			public void onBindViewHolder(DefaultViewHolder holder, int position) {
				final Store store = mEntityList.get(position);
				
				holder.title.setText(store.getCustomer().getName());
				holder.title.setTypeface(mRobotoCondensedBold);
				
				holder.subtitle.setText(store.getStoreProperty().getGoldenStatus());
				holder.subtitle.setTypeface(mRobotoCondensedLight);
				
				holder.description.setText(store.getAddress()); 
				holder.description.setTypeface(mRobotoCondensedLight);

				holder.staticmaptitle.setText(store.getChannel()); 
				holder.staticmaptitle.setTypeface(mRobotoCondensedLight);
				
				holder.itemView.setOnClickListener(new OnClickListener() { 
					
					@Override
					public void onClick(View view) {
						StoreParcel storeParcel = new StoreParcel(store);
						
						Bundle arguments = new Bundle();
						arguments.putParcelable("account", mAccount);
						arguments.putParcelable("store", storeParcel);   

						Fragment fragment = new StoreFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				holder.staticmap.setMappable(store); 
			}
			
		};
	}
	
	@Override
	protected void refreshData() {
		new DataLoader() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					CustomerParcel customerParcel = (CustomerParcel) getArguments().getParcelable("customer");
					
					mQueryBuilder = mSecuredStorage.getStoreDao().queryBuilder();
					
					Where<Store, Integer> where = mQueryBuilder.where();
					where.like("name", mQueryText)
							.or()
						.like("address", mQueryText);
					
					if (customerParcel != null) {
						where.and().eq("customer", customerParcel.getCustomer().getId());
					}
					
					mQueryBuilder.orderBy("name", true);
					
					mEntityList.addAll( mQueryBuilder.query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute();
	}
	
}
