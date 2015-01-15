package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityFragment;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoreFragment extends BaseEntityFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("targets").setIndicator(getTabIndicator(getString(R.string.targets))), TargetFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("location").setIndicator(getTabIndicator(getString(R.string.location))), MapFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("location2").setIndicator(getTabIndicator(getString(R.string.location))), LocationFragment.class, getArguments());

		return mFragmentTabHost;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new InitializeEntity().execute();
	}
	
    private class InitializeEntity extends AsyncTask<Void, Void, Store> {

    	@Override
    	protected void onPreExecute() {
    		mEntityCard.setVisibility(View.GONE); 
    	}
    	
		@Override
		protected Store doInBackground(Void... params) {
			Store store = getArguments().getParcelable("store");
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				store = securedStorage.getStoreDao().queryForId(store.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return store;
		}
		
		@Override
		protected void onPostExecute(Store store) {
			if (store != null) { 
				TextView title = (TextView) mEntityCard.findViewById(R.id.title);
				title.setText(store.getCustomer().getName());
				title.setTypeface(mRobotoCondensedBold);
				
				TextView subtitle = (TextView) mEntityCard.findViewById(R.id.subtitle);
				subtitle.setText(store.getStoreProperty().getGoldenStatus());
				subtitle.setTypeface(mRobotoCondensedLight);
				
				TextView description = (TextView) mEntityCard.findViewById(R.id.description);
				description.setText(store.getAddress()); 
				description.setTypeface(mRobotoCondensedLight);

				TextView staticmaptitle = (TextView) mEntityCard.findViewById(R.id.staticmaptitle);
				staticmaptitle.setText(store.getChannel()); 
				staticmaptitle.setTypeface(mRobotoCondensedLight);
				
				StaticMapView staticmap = (StaticMapView) mEntityCard.findViewById(R.id.staticmap);
				staticmap.setMappable(store); 
				
				mEntityCard.setVisibility(View.VISIBLE); 
	        }
		}
    	
    }
	
}
