package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.view.RouteView;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

import com.j256.ormlite.dao.Dao;

public class PsrListFragment extends BaseListFragment {
	
	private List<Psr> mGroupData = new ArrayList<Psr>();
	private List<List<Route>> mChildData = new ArrayList<List<Route>>();
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.psr_fragment_menu, menu);
		
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAdapter = new MyAdapter();
		
		setListAdapter(mAdapter);
		
		new LoadData().execute();
 	}
	
	private class LoadData extends BaseDataLoader {

		private Dao<Psr, String> mPsrDao;
		private Dao<Route, String> mRouteDao;
		
		public LoadData() {
			mPsrDao = getDbHelper().getPsrDao();
			mRouteDao = getDbHelper().getRouteDao();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			mGroupData.clear();
			mChildData.clear();

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
		
	}
	
	private class MyAdapter extends BaseExpandableListAdapter {
		
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
			
			TextView title = (TextView) relativeLayout.findViewById(R.id.title); 
			title.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			title.setText(psr.getName());   
			
			TextView subtitle = (TextView) relativeLayout.findViewById(R.id.subtitle); 
			subtitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			subtitle.setText(psr.getBranch().getName());  
			
			TextView description = (TextView) relativeLayout.findViewById(R.id.description); 
			description.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			description.setText(psr.getDepartment().getName()); 
			
			StaticMapView staticmap = (StaticMapView) relativeLayout.findViewById(R.id.staticmap); 
			staticmap.setMappable(psr); 
			staticmap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {				
				}
				
			});
			
			TextView staticmaptitle = (TextView) relativeLayout.findViewById(R.id.staticmaptitle); 
			staticmaptitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());  
			staticmaptitle.setText(Text.prepareAddress(psr.getProject())); 
			
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
			return new RouteView(getActivity(), (Route) getChild(groupPosition, childPosition)); 
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
