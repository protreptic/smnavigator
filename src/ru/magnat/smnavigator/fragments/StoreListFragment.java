package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.db.MainDbHelper;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.entities.StoreStatistics;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class StoreListFragment extends ExpandableListFragment {

	private MainDbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Store, String> mStoreDao;
	private List<Store> mGroupData = new ArrayList<Store>();
	private List<StoreStatistics> mChildData = new ArrayList<StoreStatistics>();
	
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
					StoreStatistics storeStatistics;
					
					if ((storeStatistics = store.getStoreStatistics()) != null) {
						mChildData.add(storeStatistics);	
					}
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
		}
		
		@Override
		public int getGroupCount() {	
			return mGroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {		
			return mChildData.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildData.get(groupPosition); 
		}

		@Override
		public long getGroupId(int groupPosition) {
			return ((Store) mGroupData.get(groupPosition)).getId();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return ((StoreStatistics) mChildData.get(groupPosition)).getId();
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
			
			Button link = new Button(getActivity());
			link.setText("Показать на карте"); 
			link.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), MainActivity.class);
					
					Bundle bundle = new Bundle();
					bundle.putDouble("latitude", store.getLatitude()); 
					bundle.putDouble("longitude", store.getLongitude()); 
					
					intent.putExtras(bundle);
					
					getActivity().startActivity(intent); 
				}
				
			});
			
			linearLayout.addView(name);
			linearLayout.addView(address);
			linearLayout.addView(link);
			
			return linearLayout;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			
			
			return null;
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
