package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.entities.StoreStatistics;
import ru.magnat.smnavigator.util.DateUtils;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends ExpandableListFragment {

	private MainDbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Store, String> mStoreDao;
	private Dao<StoreStatistics, String> mStoreStatisticsDao;
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<List<StoreStatistics>> mChildData = new ArrayList<List<StoreStatistics>>();
	public static final SparseArray<Drawable> drawables = new SparseArray<Drawable>();
	
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
				
				new LoadData().execute();
				
				return true;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getExpandableListView().setPadding(5, 5, 5, 5);
 		getExpandableListView().setGroupIndicator(null); 
		
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
				List<Store> stores = mStoreDao.queryBuilder().where().like("name", mQueryText).or().like("address", mQueryText).query(); 
				
				mGroupData.addAll(stores);
				
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
						
			RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.store_view, parent, false);
			
			TextView name = (TextView) relativeLayout.findViewById(R.id.name); 
			name.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			name.setText(store.getName());  
			
			TextView address = (TextView) relativeLayout.findViewById(R.id.address); 
			address.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			address.setText(Text.prepareAddress(store.getAddress())); 
			
			ImageView location = (ImageView) relativeLayout.findViewById(R.id.imageView1); 
			location.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {				
					Intent intent = new Intent(MainActivity.ACTION_MOVE);

					intent.putExtra("latitude", store.getLatitude()); 
					intent.putExtra("longitude", store.getLongitude()); 
					intent.putExtra("zoom", 15); 
					
					getActivity().sendBroadcast(intent); 
					getActivity().finish();
				}
				
			});
			
			return new StaticMapView(getActivity(), store);
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			StoreStatistics storeStatistics = (StoreStatistics) getChild(groupPosition, childPosition);
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setPadding(35, 5, 5, 5); 
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			
			TextView totalDistribution = new TextView(getActivity()); 
			totalDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			totalDistribution.setText("����� ������������ (���): \t\t\t\t" + storeStatistics.getTotalDistribution());  
			totalDistribution.setTextSize(16); 
			totalDistribution.setTextColor(getResources().getColor(R.color.gray));
			 
			TextView goldenDistribution = new TextView(getActivity()); 
			goldenDistribution.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			goldenDistribution.setText("������� ������������: \t\t\t\t\t" + storeStatistics.getGoldenDistribution());  
			goldenDistribution.setTextSize(16); 
			goldenDistribution.setTextColor(getResources().getColor(R.color.gray));
			
			TextView turnoverCurrentMonth = new TextView(getActivity()); 
			turnoverCurrentMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			turnoverCurrentMonth.setText("������������ �� ������� �����: \t\t" + storeStatistics.getTurnoverCurrentMonth());  
			turnoverCurrentMonth.setTextSize(16); 
			turnoverCurrentMonth.setTextColor(getResources().getColor(R.color.gray));
			
			TextView turnoverPreviousMonth = new TextView(getActivity()); 
			turnoverPreviousMonth.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			turnoverPreviousMonth.setText("������������ �� ���������� �����: \t" + storeStatistics.getTurnoverPreviousMonth());  
			turnoverPreviousMonth.setTextSize(16); 
			turnoverPreviousMonth.setTextColor(getResources().getColor(R.color.gray));
			
			TextView lastVisit = new TextView(getActivity()); 
			lastVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			lastVisit.setText("���� �������� ���������: \t\t\t\t" + DateUtils.format(storeStatistics.getLastVisit()));  
			lastVisit.setTextSize(16); 
			lastVisit.setTextColor(getResources().getColor(R.color.gray));
			
			TextView nextVisit = new TextView(getActivity()); 
			nextVisit.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			nextVisit.setText("���� ���������� ���������: \t\t\t\t" + DateUtils.format(storeStatistics.getNextVisit()));  
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
