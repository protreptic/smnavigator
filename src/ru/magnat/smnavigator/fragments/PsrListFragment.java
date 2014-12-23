package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.data.DbHelper;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.view.RouteView;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.accounts.Account;
import android.os.AsyncTask;
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
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

public class PsrListFragment extends ExpandableListFragment {
	
	private DbHelper mDbHelper;
	private MyAdapter mAdapter;
	private Dao<Psr, String> mPsrDao;
	private Dao<Route, String> mRouteDao;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		AccountHelper accountHelper = AccountHelper.get(getActivity());
		Account account = accountHelper.getCurrentAccount();
		
		//getExpandableListView().setPadding(5, 5, 5, 5);
		getExpandableListView().setGroupIndicator(null); 
		getExpandableListView().setDivider(null); 
		getExpandableListView().setDividerHeight(-5);
		getExpandableListView().setBackgroundColor(getResources().getColor(R.color.gray)); 
		//getExpandableListView().setBackground(getResources().getDrawable(R.drawable.bg_2));  
		
		mDbHelper = DbHelper.getInstance(getActivity(), account);
		
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
			
			TextView title = (TextView) relativeLayout.findViewById(R.id.title); 
			title.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			title.setText(psr.getName());   
			
			TextView subtitle = (TextView) relativeLayout.findViewById(R.id.subtitle); 
			subtitle.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			subtitle.setText(psr.getBranch().getName());  
			
			TextView description = (TextView) relativeLayout.findViewById(R.id.description); 
			description.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
			description.setText(psr.getDepartment().getName()); 
			
			StaticMapView staticmap = (StaticMapView) relativeLayout.findViewById(R.id.staticmap); 
			staticmap.setMappable(psr); 
			staticmap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {				
				}
				
			});
			
			TextView staticmaptitle = (TextView) relativeLayout.findViewById(R.id.staticmaptitle); 
			staticmaptitle.setTypeface(Fonts.getInstance(getActivity()).getDefaultTypeface());  
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
