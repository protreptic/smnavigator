package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.entities.StoreStatistics;
import ru.magnat.smnavigator.util.DateUtils;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends ExpandableListFragment {

	private MainDbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Store, String> mStoreDao;
	private Dao<StoreStatistics, String> mStoreStatisticsDao;
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<StoreStatistics>> mChildData = new ArrayList<List<StoreStatistics>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.store_fragment_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getExpandableListView().setPadding(5, 5, 5, 5);
 		
		mDbHelper = MainDbHelper.getInstance(getActivity());
		
		mAdapter = new MyAdapter();
		
		setListAdapter(mAdapter);
		setEmptyText(getResources().getString(R.string.emptyList)); 
		
		new LoadData().execute();
 	}
	
	private class LoadData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setListShown(false); 
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mGroupData.clear();
			mChildData.clear();

			try {
				mGroupData.addAll(mStoreDao.queryForAll());
				
				for (Store store : mGroupData) {
					StoreStatistics storeStatistics = mStoreStatisticsDao.queryForId(store.getId().toString());
					
					List<StoreStatistics> statistics = new ArrayList<StoreStatistics>();
					if (storeStatistics != null) {
						statistics.add(storeStatistics);
					}
					
					mChildData.add(statistics);	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			
			setListShown(true); 
		}
		
	}
	
	private class MyAdapter extends BaseExpandableListAdapter {
		
		public MyAdapter() {
			mStoreDao = mDbHelper.getStoreDao();
			mStoreStatisticsDao = mDbHelper.getStoreStatisticsDao();
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
			return ((StoreStatistics) mChildData.get(groupPosition).get(childPosition)).getId();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return (StoreStatistics) mChildData.get(groupPosition).get(childPosition);
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final Store store = (Store) getGroup(groupPosition);
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setPadding(35, 5, 5, 5); 
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			
			TextView name = new TextView(getActivity()); 
			name.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			name.setText(store.getName());  
			name.setTextSize(18); 
			
			TextView address = new TextView(getActivity()); 
			address.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			address.setText(Text.prepareAddress(store.getAddress())); 
			address.setTextSize(15); 
			
//			ImageButton link = new ImageButton(getActivity());
//			link.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place));
//			link.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View view) {				
//					Intent intent = new Intent(MainActivity.ACTION_MOVE);
//
//					intent.putExtra("latitude", store.getLatitude()); 
//					intent.putExtra("longitude", store.getLongitude()); 
//					intent.putExtra("zoom", 15); 
//					
//					getActivity().sendBroadcast(intent); 
//					getActivity().finish();
//				}
//				
//			});

			linearLayout.addView(name);
			linearLayout.addView(address);
			//linearLayout.addView(link);
			
			return linearLayout;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			StoreStatistics storeStatistics = (StoreStatistics) getChild(groupPosition, childPosition);
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setPadding(35, 5, 5, 5); 
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			
			TextView totalDistribution = new TextView(getActivity()); 
			totalDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			totalDistribution.setText("Общая дистрибьюция (ОПД): \t\t\t\t" + storeStatistics.getTotalDistribution());  
			totalDistribution.setTextSize(16); 
			totalDistribution.setTextColor(getResources().getColor(R.color.gray));
			 
			TextView goldenDistribution = new TextView(getActivity()); 
			goldenDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			goldenDistribution.setText("Золотая дистрибьюция: \t\t\t\t\t" + storeStatistics.getGoldenDistribution());  
			goldenDistribution.setTextSize(16); 
			goldenDistribution.setTextColor(getResources().getColor(R.color.gray));
			
			TextView turnoverCurrentMonth = new TextView(getActivity()); 
			turnoverCurrentMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			turnoverCurrentMonth.setText("Товарооборот за текущий месяц: \t\t" + storeStatistics.getTurnoverCurrentMonth());  
			turnoverCurrentMonth.setTextSize(16); 
			turnoverCurrentMonth.setTextColor(getResources().getColor(R.color.gray));
			
			TextView turnoverPreviousMonth = new TextView(getActivity()); 
			turnoverPreviousMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			turnoverPreviousMonth.setText("Товарооборот за предыдущий месяц: \t" + storeStatistics.getTurnoverPreviousMonth());  
			turnoverPreviousMonth.setTextSize(16); 
			turnoverPreviousMonth.setTextColor(getResources().getColor(R.color.gray));
			
			TextView lastVisit = new TextView(getActivity()); 
			lastVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			lastVisit.setText("Дата прошлого посещения: \t\t\t\t" + DateUtils.format(storeStatistics.getLastVisit()));  
			lastVisit.setTextSize(16); 
			lastVisit.setTextColor(getResources().getColor(R.color.gray));
			
			TextView nextVisit = new TextView(getActivity()); 
			nextVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			nextVisit.setText("Дата следующего посещения: \t\t\t\t" + DateUtils.format(storeStatistics.getNextVisit()));  
			nextVisit.setTextSize(16); 
			nextVisit.setTextColor(getResources().getColor(R.color.gray));
			
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

}
