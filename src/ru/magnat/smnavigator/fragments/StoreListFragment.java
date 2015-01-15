package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEndlessListFragment;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.synchronization.SynchronizationListener;
import ru.magnat.smnavigator.synchronization.SynchronizationStatus;
import ru.magnat.smnavigator.view.TargetView;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.accounts.Account;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends BaseEndlessListFragment implements OnScrollListener, SynchronizationListener {

	@Override
	public void onResume() {
		super.onResume();
		
		((MainActivity) getActivity()).registerSyncListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		((MainActivity) getActivity()).unregisterSyncListener(this);
	}
	
	@Override
	public void onSynchronizationCompleted(SynchronizationStatus status) {
		switch (status) {
			case STARTED: {} break;
			case ACK: {} break;
			case COMPLETED: {
				new LoadData().execute();
			} break;
			case CANCELED: {
				new LoadData().execute();
			} break;
			case ERROR: {
				new LoadData().execute();
			} break;
			default: {} break;
		}
	}
	
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<Target>> mChildData = new ArrayList<List<Target>>();
	
	private Account mAccount;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		
		progressBar = new ProgressBar(getActivity());
		
		getExpandableListView().setOnScrollListener(this);
		getExpandableListView().addFooterView(progressBar); 
		
		mAdapter = new MyAdapter();
		
		setListAdapter(mAdapter);
		
		new LoadData().execute();
 	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.store_fragment, menu);
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch));
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (!TextUtils.isEmpty(query) && query.length() > 3) { 
					mQueryText = "%" + query + "%";
				} else {
					mQueryText = "%%";
				}
				
				mGroupData.clear();
				mChildData.clear();
				offset = 0;
				count = 0;
				
				if (progressBar == null) {
					progressBar.setVisibility(View.VISIBLE);
				}
				
				new LoadData().execute();
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}
	
	private class LoadData extends AsyncTask<Void, Void, Void> {
		
		private Dao<Store, String> mStoreDao;
		private Dao<Target, String> mTargetDao;
		
		public LoadData() {
			mStoreDao = getDbHelper().getStoreDao();
			mTargetDao = getDbHelper().getTargetDao();
		}
		
		@Override
		protected void onPreExecute() {
			if (offset == 0) {
				setListShown(false); 
			}
			
			mIsLoading = true;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try { 
				if (count == 0) 
					count = mStoreDao.queryBuilder().where().like("name", mQueryText).or().like("address", mQueryText).countOf();
				
				List<Store> stores = mStoreDao.queryBuilder().offset(offset).limit(limit).where().like("name", mQueryText).or().like("address", mQueryText).query();
				
				offset += stores.size();
				
				mGroupData.addAll(stores);
				
				for (Store store : stores) {
					mChildData.add(mTargetDao.queryForEq("store", store.getId().toString()));	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			
			if (isListShown() == false) {
				setListShown(true); 
			}
			
	        if (count == getExpandableListView().getCount() - 1) {
	        	progressBar.setVisibility(View.GONE); 
	        }
	        
	        mIsLoading = false;
		}
		
	}
	
	private class MyAdapter extends BaseExpandableListAdapter {
		
		@Override
		public int getGroupCount() {	
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return ((Store) mGroupData.get(groupPosition)).getId();
		}
		
		@Override
		public Object getGroup(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {		
			return mChildData.get(groupPosition).size();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return ((Target) mChildData.get(groupPosition).get(childPosition)).getStore().getId();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return (Target) mChildData.get(groupPosition).get(childPosition);
		}
 
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final Store store = (Store) getGroup(groupPosition);
			
			RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.default_list_item, parent, false);
			
			TextView name = (TextView) relativeLayout.findViewById(R.id.title); 
			name.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			name.setText(store.getCustomer().getName());   
			
			TextView address = (TextView) relativeLayout.findViewById(R.id.description); 
			address.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			address.setText(Text.prepareAddress(store.getAddress())); 
			
			TextView channel = (TextView) relativeLayout.findViewById(R.id.staticmaptitle); 
			channel.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			channel.setText(store.getChannel()); 
			
			TextView goldenStatus = (TextView) relativeLayout.findViewById(R.id.subtitle); 
			goldenStatus.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			goldenStatus.setText(store.getStoreProperty().getGoldenStatus()); 
			
			StaticMapView staticMapView = (StaticMapView) relativeLayout.findViewById(R.id.staticmap); 
			staticMapView.setMappable(store); 
			staticMapView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					double latitude = store.getLatitude();
					double longitude = store.getLongitude();
					
					if (latitude == 0 || longitude == 0) {
						Toast.makeText(getActivity(), getString(R.string.locationUnavailable), Toast.LENGTH_LONG).show(); return;
					}
					
					Bundle arguments = new Bundle();
			        arguments.putParcelable("account", mAccount); 
			        arguments.putBoolean("moveCamera", true);
			        arguments.putDouble("latitude", latitude); 
			        arguments.putDouble("longitude", longitude); 
			        
			        Fragment fragment = new MapFragment();
			        fragment.setArguments(arguments); 
			        
			        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			        fragmentTransaction.replace(R.id.content_frame, fragment);
			        fragmentTransaction.addToBackStack(null);
			        fragmentTransaction.commit();
				}
			});
			
			return relativeLayout; 
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			Target target = (Target) getChild(groupPosition, childPosition);
			
			return new TargetView(getActivity(), target); 
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
		@Override
		public boolean hasStableIds() {
			return false;
		}
		
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) { 
			if (getExpandableListView().getFirstVisiblePosition() == 0) {
			}
			
	        if (getExpandableListView().getLastVisiblePosition() >= getExpandableListView().getCount() - 1 && !mIsLoading) {
	        	if (count != offset) {
	        		new LoadData().execute();
	        	} else {
	        		if (progressBar != null) {
	        			progressBar.setVisibility(View.GONE); 
	        		}
	        	}
	        }
	    }
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

	@Override
	public void onInitialSynchronizationCompleted(SynchronizationStatus status) {
		// TODO Auto-generated method stub
		
	}
	
}
