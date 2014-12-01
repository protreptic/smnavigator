package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.Route;
import ru.magnat.smnavigator.entities.Store;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

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
				List<Psr> psrs = mPsrDao.queryBuilder().where().like("name", mQueryText).or().like("project", mQueryText).query(); 

				mGroupData.addAll(psrs);
				
//				for (Psr psr : mGroupData) {
//					List<Route> routes = mRouteDao.queryForEq("psr", psr.getId().toString());
//					if (!routes.isEmpty()) {
//						mChildData.add(routes);
//					}
//				}
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
			
			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setPadding(35, 5, 5, 5); 
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			
			TextView name = new TextView(getActivity()); 
			name.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			name.setText(psr.getName());  
			name.setTextSize(18); 
			
			TextView project = new TextView(getActivity()); 
			project.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			project.setText(psr.getProject()); 
			project.setTextSize(15); 
			
			ImageView location = new ImageView(getActivity());
			location.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place));
			location.setLayoutParams(new LayoutParams(48, 48)); 
			location.setBackground(getResources().getDrawable(R.drawable.button_selector));
			location.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {				
					Intent intent = new Intent(MainActivity.ACTION_MOVE);

					intent.putExtra("latitude", psr.getLatitude()); 
					intent.putExtra("longitude", psr.getLongitude()); 
					intent.putExtra("zoom", 15); 
					
					getActivity().sendBroadcast(intent); 
					getActivity().finish();
				}
				
			});
			
			linearLayout.addView(name);
			linearLayout.addView(project);
			linearLayout.addView(location);
			
			return linearLayout;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			Route route = (Route) getChild(groupPosition, childPosition);
			
			try {
				final Store store = mStoreDao.queryForId(route.getStore().toString()); 
				
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
				
				ImageButton link = new ImageButton(getActivity());
				link.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place));
				link.setOnClickListener(new OnClickListener() {
					
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
	
				linearLayout.addView(name);
				linearLayout.addView(address);
				linearLayout.addView(link);
			
				return linearLayout;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
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
