package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityFragment;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomerFragment extends BaseEntityFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("stores").setIndicator(getTabIndicator(getString(R.string.titleStores))), StoreListFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty1").setIndicator(getTabIndicator("empty1")), EmptyFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty2").setIndicator(getTabIndicator("empty2")), EmptyFragment.class, getArguments());

		return mFragmentTabHost;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new InitializeEntity().execute();
	}
	
    private class InitializeEntity extends AsyncTask<Void, Void, Customer> {

    	@Override
    	protected void onPreExecute() {
    		mEntityCard.setVisibility(View.GONE); 
    	}
    	
		@Override
		protected Customer doInBackground(Void... params) {
			Customer customer = getArguments().getParcelable("customer");
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				customer = securedStorage.getCustomerDao().queryForSameId(customer);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return customer;
		}
		
		@Override
		protected void onPostExecute(Customer customer) {
			if (customer != null) { 
				TextView title = (TextView) mEntityCard.findViewById(R.id.title);
				title.setText(customer.getName());
				title.setTypeface(mRobotoCondensedBold);
				
				TextView subtitle = (TextView) mEntityCard.findViewById(R.id.subtitle);
				subtitle.setVisibility(View.GONE);
				
				TextView description = (TextView) mEntityCard.findViewById(R.id.description);
				description.setVisibility(View.GONE); 
				
				StaticMapView staticmap = (StaticMapView) mEntityCard.findViewById(R.id.staticmap);
				staticmap.setVisibility(View.GONE); 
				
				TextView staticmaptitle = (TextView) mEntityCard.findViewById(R.id.staticmaptitle);
				staticmaptitle.setVisibility(View.GONE); 
				
				mEntityCard.setVisibility(View.VISIBLE); 
	        }
		}
    	
    }
	
}
