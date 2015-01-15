package ru.magnat.smnavigator.fragments.experimental;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.accounts.Account;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class ShopFragmentW extends Fragment {

	private Account mAccount;
	
	private RecyclerView mRecyclerView;
	private ShopAdapter	mAdapter;
	private static Typeface sTypeface;
	
	private String mQueryText = "%%";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.psr_fragment, menu);
		
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sTypeface = Fonts.get(getActivity()).getDefaultTypeface();
		
		mAccount = getArguments().getParcelable("account");
		
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		
		mAdapter = new ShopAdapter();
		
		mRecyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter); 
		
		new LoadData().execute();
	}
	
	private List<Store> mShops = new ArrayList<Store>();
	
	public class ShopAdapter extends RecyclerView.Adapter<PsrViewHolder> {
		
		@Override
		public int getItemCount() {
			return mShops.size();
		}

		@Override
		public void onBindViewHolder(PsrViewHolder holder, int position) {
			Store store = mShops.get(position);
			
			holder.title.setText(store.getCustomer().getName());
			holder.subtitle.setText("");
			holder.description.setText(Text.prepareAddress(store.getAddress())); 
			holder.staticmap.setMappable(store); 
			holder.staticmaptitle.setText(store.getChannel());
		}

		@Override
		public PsrViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item_cardview, parent, false);
			
			return new PsrViewHolder(itemView);
		}
		
	}
	
	public class LoadData extends AsyncTask<Void, Void, Void> {

		private Dao<Store, Integer> mStoreDao;
		
		public LoadData() {
			mStoreDao = SecuredStorage.get(getActivity(), mAccount).getStoreDao();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mShops.clear();

			try {
				List<Store> shops = mStoreDao.queryBuilder().where().like("name", "%" + mQueryText + "%").or().like("address", "%" + mQueryText + "%").query();
				
				mShops.addAll(shops);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	public static class PsrViewHolder extends RecyclerView.ViewHolder {

		protected StaticMapView staticmap;
		
		protected TextView title;
		protected TextView subtitle;
		protected TextView description;
		protected TextView staticmaptitle;
		
		public PsrViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			title.setTypeface(sTypeface);  
			
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			subtitle.setTypeface(sTypeface);  
			
			description = (TextView) itemView.findViewById(R.id.description);
			description.setTypeface(sTypeface); 
			
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
			staticmaptitle.setTypeface(sTypeface); 
		}

	}

}
