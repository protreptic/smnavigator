package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityListFragment;
import ru.magnat.smnavigator.model.Customer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomerListFragment extends BaseEntityListFragment<Customer> {

	@Override
	protected Adapter<?> createAdapter() {
		return new DefaultAdapter() {
			
			@Override
			public void onBindViewHolder(DefaultViewHolder holder, int position) {
				final Customer customer = mEntityList.get(position);
				
				holder.title.setText(customer.getName());
				holder.title.setTypeface(mRobotoCondensedBold);
				
				holder.itemView.setOnClickListener(new OnClickListener() { 
					
					@Override
					public void onClick(View view) {
						Bundle arguments = new Bundle();
						arguments.putParcelable("account", mAccount);
						arguments.putParcelable("customer", customer);   

						Fragment fragment = new CustomerFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				holder.subtitle.setVisibility(View.GONE);
				holder.description.setVisibility(View.GONE); 
				holder.staticmap.setVisibility(View.GONE); 
				holder.staticmaptitle.setVisibility(View.GONE); 
			}
			
		};
	}
	
	@Override
	protected void refreshData() {
		new DataLoader() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					mQueryBuilder = mSecuredStorage.getCustomerDao().queryBuilder();
					mQueryBuilder.where()
						.like("name", mQueryText);
					mQueryBuilder.orderBy("name", true);
					
					mEntityList.addAll( mQueryBuilder.query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute();
	}

	@Override
	protected Class<Customer> getType() {
		return Customer.class;
	}
	
}
