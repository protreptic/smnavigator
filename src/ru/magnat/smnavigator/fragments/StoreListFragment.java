package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.view.StoreView;
import ru.magnat.smnavigator.view.TargetView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView.OnQueryTextListener;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends BaseEndlessListFragment implements OnScrollListener {

	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<Target>> mChildData = new ArrayList<List<Target>>();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		progressBar = new ProgressBar(getActivity());
		
		getExpandableListView().setOnScrollListener(this);
		getExpandableListView().addFooterView(progressBar); 
		
		mAdapter = new MyAdapter();
		
		setListAdapter(mAdapter);
		
		new LoadData().execute();
 	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.psr_fragment_menu, menu);
		
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (TextUtils.isEmpty(query) || query.length() < 3) {
					return false;
				}
				
				mQueryText = query;
				
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
					count = mStoreDao.queryBuilder().where().like("name", "%" + mQueryText + "%").or().like("address", "%" + mQueryText + "%").countOf();
				
				List<Store> stores = mStoreDao.queryBuilder().offset(offset).limit(limit).where().like("name", "%" + mQueryText + "%").or().like("address", "%" + mQueryText + "%").query();
				
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
			return new StoreView(getActivity(), (Store) getGroup(groupPosition)); 
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
	
}
