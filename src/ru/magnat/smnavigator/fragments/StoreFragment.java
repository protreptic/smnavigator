package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import org.javaprotrepticon.android.widgetutils.widget.StaticMapView;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityFragment;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.parcel.StoreParcel;
import ru.magnat.smnavigator.storage.SecuredStorage;
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
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty1").setIndicator(getTabIndicator("empty1")), EmptyFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty2").setIndicator(getTabIndicator("empty2")), EmptyFragment.class, getArguments());

		return mFragmentTabHost;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new InitializeEntity().execute();
	}
	
    private class InitializeEntity extends AsyncTask<Void, Void, Store> {

    	private Store mStore;
    	
    	public InitializeEntity() {
    		mStore = ((StoreParcel) getArguments().getParcelable("store")).getStore();
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		mEntityCard.setVisibility(View.GONE); 
    	}
    	
		@Override
		protected Store doInBackground(Void... params) {
			Store store = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				store = securedStorage.getStoreDao().queryForId(mStore.getId());
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
