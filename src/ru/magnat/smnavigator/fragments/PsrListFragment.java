package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.view.StoreView;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class PsrListFragment extends ExpandableListFragment {
	
	private MainDbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Psr, String> mPsrDao;
	private Dao<Route, String> mRouteDao;
	private Dao<Store, String> mStoreDao;
	private List<Psr> mGroupData = new ArrayList<Psr>();
	private List<List<Route>> mChildData = new ArrayList<List<Route>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	private String mQueryText = "%%";
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.psr_fragment_menu, menu);
		
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
		
		//getExpandableListView().setPadding(5, 5, 5, 5);
		getExpandableListView().setGroupIndicator(null); 
		getExpandableListView().setDivider(null); 
		getExpandableListView().setDividerHeight(-5);
		getExpandableListView().setBackgroundColor(getResources().getColor(R.color.gray)); 
		//getExpandableListView().setBackground(getResources().getDrawable(R.drawable.bg_2));  
		
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
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			try {
				List<Psr> psrs = mPsrDao.queryBuilder().where().like("name", mQueryText).or().like("project", mQueryText).query(); 

				mGroupData.addAll(psrs);
				
				for (Psr psr : psrs) {
					List<Route> routes = mRouteDao.queryForEq("psr", psr.getId().toString());
					if (!routes.isEmpty()) {			
						mChildData.add(routes);
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
			mPsrDao = mDbHelper.getPsrDao();
			mRouteDao = mDbHelper.getRouteDao();
			mStoreDao = mDbHelper.getStoreDao();
		}
		
		@Override
		public int getGroupCount() {	
			return mGroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {		
			return mChildData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildData.get(groupPosition).get(childPosition);  
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return ((Route) mChildData.get(groupPosition).get(childPosition)).getId(); 
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final Psr psr = (Psr) getGroup(groupPosition);
			
			RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.default_list_item, parent, false);
			
			TextView name = (TextView) relativeLayout.findViewById(R.id.title); 
			name.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			name.setText(psr.getName());   
			
			TextView address = (TextView) relativeLayout.findViewById(R.id.description); 
			address.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			address.setText(psr.getDepartment()); 
			
			TextView channel = (TextView) relativeLayout.findViewById(R.id.staticmaptitle); 
			channel.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			channel.setText(Text.prepareAddress(psr.getProject())); 
			
			TextView goldenStatus = (TextView) relativeLayout.findViewById(R.id.subtitle); 
			goldenStatus.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			goldenStatus.setText(Text.prepareAddress(psr.getBranch())); 
			
			StaticMapView staticMapView = (StaticMapView) relativeLayout.findViewById(R.id.staticmap); 
			staticMapView.setMappable(psr); 
			staticMapView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {				
					Intent intent = new Intent(MainActivity.ACTION_MOVE);

					intent.putExtra("latitude", psr.getLatitude()); 
					intent.putExtra("longitude", psr.getLongitude()); 
					intent.putExtra("zoom", 10); 
					
					getActivity().sendBroadcast(intent); 
					getActivity().finish();
				}
				
			});
			
			ImageView details = (ImageView) relativeLayout.findViewById(R.id.actions); 
			details.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
						
				}
			});
			
			return relativeLayout;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			Route route = (Route) getChild(groupPosition, childPosition);
			
			Store store = null;  
			
			try {
				store = mStoreDao.queryForId(route.getStore().toString());  
			} catch (SQLException e) {
				
			}
			
//			LinearLayout linearLayout = new LinearLayout(getActivity());
//			linearLayout.setPadding(5, 5, 5, 5); 
//			linearLayout.setOrientation(LinearLayout.VERTICAL); 
//			
//			TextView name = new TextView(getActivity()); 
//			name.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
//			name.setText(route.getVisitDate().toString());  
//			name.setTextSize(18); 

//			linearLayout.addView(name);
		
			if (store != null) {
				return new StoreView(getActivity(), store); 
			}
			
			return new RelativeLayout(getActivity());
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
