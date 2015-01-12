package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.DbHelperSecured;
import ru.magnat.smnavigator.map.LocationHelper;
import ru.magnat.smnavigator.model.Store;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;

public class MapFragment extends SupportMapFragment {
	
	private LocationHelper mLocationHelper;
	private Account mAccount;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		GoogleMap map = getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				if (location != null) {
					getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10f));
					getMap().setOnMyLocationChangeListener(null); 
				}
			}
		});
		
		mAccount = getArguments().getParcelable("account");
		mSearchAdapter = new SearchAdapter();
		
		mLocationHelper = LocationHelper.get(getActivity(), map, mAccount);
	}	
	 
	private String mQueryText = "%%";
	
	@Override
	public void onStart() {
		super.onStart();
		
		mLocationHelper.updateOverlays();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		LocationHelper.destroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	public void updateMap() {
		if (mLocationHelper != null) 
			mLocationHelper.updateOverlays();
	}
	
	public class LoadData extends AsyncTask<Void, Void, Void> {

		private Dao<Store, String> mStoreDao;
		
		public LoadData() {
			mStoreDao = DbHelperSecured.get(getActivity(), mAccount).getStoreDao();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mShops.clear();

			try {
				List<Store> shops = mStoreDao.queryBuilder().where().like("name", mQueryText).or().like("address", mQueryText).query();
				
				mShops.addAll(shops);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSearchAdapter.notifyDataSetChanged();
			
			showSearchDialog();
		}
		
	}
	
	private List<Store> mShops = new ArrayList<Store>();
	private SearchAdapter mSearchAdapter;
	
	private class SearchAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mShops.size();
		}

		@Override
		public Object getItem(int position) {
			return mShops.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mShops.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.account_item, parent, false);
				
				Typeface typeface = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Regular");
				
				holder = new ViewHolder();
				holder.line1 = (TextView) convertView.findViewById(R.id.line1);
				holder.line1.setTypeface(typeface);
				
				holder.line2 = (TextView) convertView.findViewById(R.id.line2);
				holder.line2.setTypeface(typeface);
				
				convertView.setTag(holder); 
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Store store = (Store) getItem(position);
			
			holder.line1.setText(store.getCustomer().getName());
			holder.line2.setText(Text.prepareAddress(store.getAddress())); 
			
			return convertView;
		}
		
	}
	
	private static class ViewHolder {
		TextView line1;
		TextView line2;
	}
	
	private void showSearchDialog() {
		if (mShops.isEmpty()) {
			Toast.makeText(getActivity(), getString(R.string.emptyList), Toast.LENGTH_LONG).show(); return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
        	.setTitle(null)
        	.setSingleChoiceItems(mSearchAdapter, -1, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int position) {
					dialog.dismiss();
					
					mLocationHelper.showShop(mShops.get(position)); 
				}
			})
			.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					
				}
			})
            .show();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_fragment, menu);
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch));
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				mQueryText = "%" + query + "%";
				
				new LoadData().execute();
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}
	
}
