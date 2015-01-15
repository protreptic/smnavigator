package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityFragment;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PsrFragment extends BaseEntityFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("routes").setIndicator(getTabIndicator(getString(R.string.routes))), RouteFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty1").setIndicator(getTabIndicator("empty1")), EmptyFragment.class, getArguments());
		mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("empty2").setIndicator(getTabIndicator("empty2")), EmptyFragment.class, getArguments());
		
		return mFragmentTabHost;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new InitializeEntity().execute();
	}
	
    private class InitializeEntity extends AsyncTask<Void, Void, Psr> {
    	
    	@Override
    	protected void onPreExecute() {
    		mEntityCard.setVisibility(View.GONE); 
    	}
    	
		@Override
		protected Psr doInBackground(Void... params) {
			Psr psr = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				psr = securedStorage.getPsrDao().queryForId(getArguments().getInt("psr_id"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return psr;
		}
		
		@Override
		protected void onPostExecute(Psr psr) {
			if (psr != null) { 
				TextView title = (TextView) mEntityCard.findViewById(R.id.title);
				title.setText(psr.getName());
				title.setTypeface(mRobotoCondensedBold);
				
				TextView subtitle = (TextView) mEntityCard.findViewById(R.id.subtitle);
				subtitle.setText(psr.getBranch().getName());
				subtitle.setTypeface(mRobotoCondensedLight);
				
				TextView description = (TextView) mEntityCard.findViewById(R.id.description);
				description.setText(psr.getDepartment().getName()); 
				description.setTypeface(mRobotoCondensedLight);

				TextView staticmaptitle = (TextView) mEntityCard.findViewById(R.id.staticmaptitle);
				staticmaptitle.setText(psr.getProject()); 
				staticmaptitle.setTypeface(mRobotoCondensedLight);
				
				StaticMapView staticmap = (StaticMapView) mEntityCard.findViewById(R.id.staticmap);
				staticmap.setMappable(psr); 
				
				mEntityCard.setVisibility(View.VISIBLE); 
	        }
		}
    	
    }
	
}
