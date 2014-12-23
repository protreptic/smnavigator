package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.view.StoreStatisticsView;
import ru.magnat.smnavigator.view.StoreView;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import android.accounts.Account;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends ExpandableListFragment implements OnScrollListener {

	private MainDbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Store, String> mStoreDao;
	private Dao<Measure, String> mMeasureDao;
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<Measure>> mChildData = new ArrayList<List<Measure>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	} 
	
	private String mQueryText = "";
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.store_fragment_menu, menu);
		
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				if (TextUtils.isEmpty(newText) || newText.length() < 3) {
					return false;
				}
				
				mQueryText = newText;
				
				mGroupData.clear();
				mChildData.clear();
				offset = 0;
				count = 0;
				
				if (progressBar == null) {
					progressBar = (ProgressBar) LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar, null, false); 
					progressBar.setPadding(6, -7, 6, -6); 
					
					getExpandableListView().addFooterView(progressBar); 
				}
				
				new LoadData().execute();
				
				return true;
			}
		});
	}

	private ProgressBar progressBar;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		AccountHelper accountHelper = AccountHelper.get(getActivity());
		Account account = accountHelper.getCurrentAccount();
		
		getExpandableListView().setGroupIndicator(null); 
		getExpandableListView().setDivider(null); 
		getExpandableListView().setDividerHeight(-5);
		getExpandableListView().setBackgroundColor(getResources().getColor(R.color.gray)); 
		//getExpandableListView().setBackground(getResources().getDrawable(R.drawable.endless_list_background));
		getExpandableListView().setOnScrollListener(this); 
		
		progressBar = (ProgressBar) LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar, null, false); 
		progressBar.setPadding(6, -7, 6, -6); 
		
		getExpandableListView().addFooterView(progressBar); 
		
		mDbHelper = MainDbHelper.getInstance(getActivity(), account);
		
		mAdapter = new MyAdapter();
		
		setListAdapter(mAdapter);
		setEmptyText(getResources().getString(R.string.emptyList)); 
		
		new LoadData().execute();
 	}
	
	private long offset;
	private long count;
	
	private static final long LIMIT = 15; 
	
	private class LoadData extends AsyncTask<Void, Void, Void> {

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
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			try { 
				if (count == 0) 
					count = mStoreDao.queryBuilder().where().like("name", "%" + mQueryText + "%").or().like("address", "%" + mQueryText + "%").countOf();
				
				List<Store> stores = mStoreDao.queryBuilder().offset(offset).limit(LIMIT).where().like("name", "%" + mQueryText + "%").or().like("address", "%" + mQueryText + "%").query();
				
				offset += stores.size();
				
				mGroupData.addAll(stores);
				
				for (Store store : mGroupData) {
					Measure measure = mMeasureDao.queryForId(store.getId().toString());
					
					List<Measure> measures = new ArrayList<Measure>();
					if (measure != null) {
						measures.add(measure);
					}
					
					mChildData.add(measures);	
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
        		if (progressBar != null) {
        			getExpandableListView().removeFooterView(progressBar);
        			progressBar = null;
        		}
	        }
	        
	        mIsLoading = false;
		}
		
	}
	
	private class MyAdapter extends BaseExpandableListAdapter {
		
		public MyAdapter() {
			mStoreDao = mDbHelper.getStoreDao();
			mMeasureDao = mDbHelper.getMeasureDao();
		}
		
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
			return ((Measure) mChildData.get(groupPosition).get(childPosition)).getId();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return (Measure) mChildData.get(groupPosition).get(childPosition);
		}
 
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			return new StoreView(getActivity(), (Store) getGroup(groupPosition)); 
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			Measure storeStatistics = (Measure) getChild(groupPosition, childPosition);
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			linearLayout.setBackground(getResources().getDrawable(R.drawable.frame_2)); 
			 
			StoreStatisticsView totalDistribution = new StoreStatisticsView(getActivity()); 
			totalDistribution.setTitle(storeStatistics.getTotalDistribution().toString());  
			totalDistribution.setSubTitle(getResources().getString(R.string.totalDistribution));  
			
			StoreStatisticsView goldenDistribution = new StoreStatisticsView(getActivity()); 
			goldenDistribution.setTitle(storeStatistics.getGoldenDistribution().toString());  
			goldenDistribution.setSubTitle(getResources().getString(R.string.goldenDistribution));  
			
			StoreStatisticsView turnoverCurrentMonth = new StoreStatisticsView(getActivity()); 
			turnoverCurrentMonth.setTitle(storeStatistics.getTurnoverCurrentMonth().toString());  
			turnoverCurrentMonth.setSubTitle(getResources().getString(R.string.turnoverCurrentMonth));  
			
			StoreStatisticsView turnoverPreviousMonth = new StoreStatisticsView(getActivity()); 
			turnoverPreviousMonth.setTitle(storeStatistics.getTurnoverPreviousMonth().toString());  
			turnoverPreviousMonth.setSubTitle(getResources().getString(R.string.turnoverPreviousMonth));  
			
			StoreStatisticsView lastVisit = new StoreStatisticsView(getActivity()); 
			lastVisit.setTitle(storeStatistics.getLastVisit());  
			lastVisit.setSubTitle(getResources().getString(R.string.lastVisit));  
			
			StoreStatisticsView nextVisit = new StoreStatisticsView(getActivity()); 
			nextVisit.setTitle(storeStatistics.getNextVisit());  
			nextVisit.setSubTitle(getResources().getString(R.string.nextVisit));  
			
			linearLayout.addView(totalDistribution);
			linearLayout.addView(goldenDistribution);
			linearLayout.addView(turnoverCurrentMonth);
			linearLayout.addView(turnoverPreviousMonth);
			linearLayout.addView(lastVisit);
			linearLayout.addView(nextVisit);
			
			return linearLayout;
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

	private boolean mIsLoading;
	
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
	        			getExpandableListView().removeFooterView(progressBar);
	        			progressBar = null;
	        		}
	        	}
	        }
	    }
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

}
