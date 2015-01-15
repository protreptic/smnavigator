package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityListFragment;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.experimental.parcel.StoreParcel;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class StoreListFragment extends BaseEntityListFragment {

	private String mQueryText = "%%";
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.store_fragment, menu);
		
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mRecyclerViewAdapter = new StoreAdapter();
		
		mRecyclerView.setAdapter(mRecyclerViewAdapter); 
		
		new LoadData().execute();
	}
	
	private List<Store> mStores = new ArrayList<Store>();
	
	public class StoreAdapter extends RecyclerView.Adapter<StoreViewHolder> {
		
		private Typeface mRobotoCondensedBold;
		
		public StoreAdapter() {
			mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		}
		
		@Override
		public int getItemCount() {
			return mStores.size();
		}

		@Override
		public void onBindViewHolder(StoreViewHolder holder, int position) {
			final Store store = mStores.get(position);
			
			holder.title.setText(store.getCustomer().getName());
			holder.title.setTypeface(mRobotoCondensedBold);
			
			holder.subtitle.setText(store.getStoreProperty().getGoldenStatus());
			holder.subtitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
			holder.description.setText(Text.prepareAddress(store.getAddress())); 
			holder.description.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
			holder.staticmap.setMappable(store); 
			
			holder.staticmaptitle.setText(store.getChannel());
			holder.staticmaptitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
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
		}

		@Override
		public StoreViewHolder onCreateViewHolder(ViewGroup parent, final int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item_cardview, parent, false);
			
			return new StoreViewHolder(itemView);
		}
		
	}
	
	public class LoadData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mStores.clear();

			SecuredStorage securedStorage = SecuredStorage.get(getActivity(), mAccount);
			
			Dao<Store, Integer> storeDao = securedStorage.getStoreDao();
			
			QueryBuilder<Store, Integer> queryBuilder;
			
			try {
				queryBuilder = storeDao.queryBuilder();
				queryBuilder.where()
					.like("name", mQueryText)
						.or()
					.like("address", mQueryText);
				queryBuilder.orderBy("name", true);
				
				List<Store> stores = queryBuilder.query();
				
				mStores.addAll(stores);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			SecuredStorage.close();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mRecyclerViewAdapter.notifyDataSetChanged();
		}
		
	}
	
	public static class StoreViewHolder extends RecyclerView.ViewHolder {

		protected StaticMapView staticmap;
		
		protected TextView title;
		protected TextView subtitle;
		protected TextView description;
		protected TextView staticmaptitle;
		
		public StoreViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			description = (TextView) itemView.findViewById(R.id.description);
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
		}
	}
	
}
