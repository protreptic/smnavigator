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
import ru.magnat.smnavigator.view.StoreView;
import android.accounts.Account;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment1 extends ListFragment implements OnScrollListener {

	private MainDbHelper mDbHelper;
	private MyAdapter mListAdapter;
	private Dao<Store, String> mStoreDao;
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<Measure>> mChildData = new ArrayList<List<Measure>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	private String mQueryText = "%%";
	
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
				mQueryText = "%" + newText + "%";
				
				mGroupData.clear();
				mChildData.clear();
				offset = 0;
				count = 0;
				
				if (progressBar == null) {
					progressBar = (ProgressBar) LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar, null, false); 
					progressBar.setPadding(6, -7, 6, -6); 
					
					getListView().addFooterView(progressBar); 
				}
				
				new LoadData().execute();
				
				return true;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	private ProgressBar progressBar;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		AccountHelper accountHelper = AccountHelper.get(getActivity());
		Account account = accountHelper.getCurrentAccount();
		
		getListView().setDivider(null); 
		getListView().setDividerHeight(-5);
		getListView().setBackgroundColor(getResources().getColor(R.color.gray)); 
		getListView().setOnScrollListener(this); 
		
		progressBar = (ProgressBar) LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar, null, false); 
		progressBar.setPadding(6, -7, 6, -6); 
		
		getListView().addFooterView(progressBar); 
		
		mDbHelper = MainDbHelper.getInstance(getActivity(), account);
		
		mListAdapter = new MyAdapter();
		
		getListView().setAdapter(mListAdapter);
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
					count = mStoreDao.queryBuilder().where().like("name", mQueryText).or().like("address", mQueryText).countOf();
				
				List<Store> stores = mStoreDao.queryBuilder().offset(offset).limit(LIMIT).where().like("name", mQueryText).or().like("address", mQueryText).query();
				
				offset += stores.size();
				
				mGroupData.addAll(stores);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mListAdapter.notifyDataSetChanged();
			
			setListShown(true); 
			
	        if (count == getListView().getCount() - 1) {
        		if (progressBar != null) {
        			getListView().removeFooterView(progressBar);
        			progressBar = null;
        		}
	        }
		}
		
	}
	
	private class MyAdapter extends BaseAdapter {
		
		public MyAdapter() {
			mStoreDao = mDbHelper.getStoreDao();
		}
		
		@Override
		public int getCount() {	
			return mGroupData.size();
		}

		@Override
		public long getItemId(int position) {
			return ((Store) mGroupData.get(position)).getId();
		}
		
		@Override
		public Object getItem(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}
		
		@Override
		public View getView(int groupPosition, View convertView, ViewGroup parent) {
			return new StoreView(getActivity(), (Store) getItem(groupPosition)); 
		}

//		@Override
//		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//			StoreStatistics storeStatistics = (StoreStatistics) getChild(groupPosition, childPosition);
//			
//			LinearLayout linearLayout = new LinearLayout(getActivity());
//			linearLayout.setBackground(getResources().getDrawable(R.drawable.frame_2)); 
//			linearLayout.setOrientation(LinearLayout.VERTICAL);  
//			
//			TextView totalDistribution = new TextView(getActivity()); 
//			totalDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			totalDistribution.setText("пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ (пїЅпїЅпїЅ): " + storeStatistics.getTotalDistribution());  
//			 
//			TextView goldenDistribution = new TextView(getActivity()); 
//			goldenDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			goldenDistribution.setText("пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ: " + storeStatistics.getGoldenDistribution());  
//			
//			TextView turnoverCurrentMonth = new TextView(getActivity()); 
//			turnoverCurrentMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			turnoverCurrentMonth.setText("пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ: " + storeStatistics.getTurnoverCurrentMonth());  
//			
//			TextView turnoverPreviousMonth = new TextView(getActivity()); 
//			turnoverPreviousMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			turnoverPreviousMonth.setText("пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ: " + storeStatistics.getTurnoverPreviousMonth());  
//			
//			TextView lastVisit = new TextView(getActivity()); 
//			lastVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			lastVisit.setText("пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ: " + DateUtils.format(storeStatistics.getLastVisit()));  
//			
//			TextView nextVisit = new TextView(getActivity()); 
//			nextVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			nextVisit.setText("пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ: " + DateUtils.format(storeStatistics.getNextVisit()));  
//			
//			linearLayout.addView(totalDistribution);
//			linearLayout.addView(goldenDistribution);
//			linearLayout.addView(turnoverCurrentMonth);
//			linearLayout.addView(turnoverPreviousMonth);
//			linearLayout.addView(lastVisit);
//			linearLayout.addView(nextVisit);
//			
//			return linearLayout;
//		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) { 
			if (getListView().getFirstVisiblePosition() == 0) {
			}
			
	        if (getListView().getLastVisiblePosition() >= getListView().getCount() - 1) {
	        	if (count != offset) {
	        		new LoadData().execute();
	        	} else {
	        		if (progressBar != null) {
	        			getListView().removeFooterView(progressBar);
	        			progressBar = null;
	        		}
	        	}
	        }
	    }
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

}
