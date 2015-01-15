package ru.magnat.smnavigator.fragments;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.javaprotrepticon.android.androidutils.Fonts;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

public class RouteFragment extends Fragment {
	
	private RouteAdapter mRouteAdapter;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionCalendar: {
				new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Calendar calendar = Calendar.getInstance(new Locale("ru", "RU"));
						calendar.clear();
						calendar.set(year, monthOfYear, dayOfMonth); 
						
						date = new Date(calendar.getTimeInMillis());
						
						loadRoutes();
					}
				}, 2015, 0, 19).show();
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.route_fragment, menu);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}

	private Account mAccount;
    private Psr mPsr;
    
    private Psr getPsr(int id) {
    	Psr psr = null;
    	
    	SecuredStorage dbHelper = SecuredStorage.get(getActivity(), mAccount);
		
		try {
			psr = dbHelper.getPsrDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
    	
    	return psr;
    }
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		mPsr = getPsr(getArguments().getInt("psr_id"));
		
		mRouteAdapter = new RouteAdapter();
		
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setReverseLayout(false);
		
		RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(mRouteAdapter); 
		
		Calendar calendar = Calendar.getInstance(new Locale("ru", "RU"));
		
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH); 
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		calendar.clear();
		calendar.set(year, monthOfYear, dayOfMonth); 
		
		date = new Date(calendar.getTimeInMillis());
		
		loadRoutes();
	}
	
	public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {
		
		private Typeface mRobotoCondensedBold;
		
		public RouteAdapter() {
			mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		}
		
		@Override
		public int getItemCount() {
			return mRoutes.size();
		}

		@Override
		public void onBindViewHolder(RouteViewHolder holder, int position) {
			Route route = mRoutes.get(position);
			
			holder.title.setTypeface(mRobotoCondensedBold);  
			holder.title.setText(route.getStore().getCustomer().getName());
			
			SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
			
			holder.subtitle.setTypeface(mRobotoCondensedBold);  
			holder.subtitle.setText(format.format(route.getVisitDate()));
			
			holder.description.setTypeface(mRobotoCondensedBold);  
			holder.description.setText(route.getStore().getAddress());
		}

		@Override
		public RouteViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout, parent, false);
			
			return new RouteViewHolder(itemView);
		}
		
	}
	
	private List<Route> mRoutes = new ArrayList<Route>();
	
	private Date date;
	
	private void loadRoutes() {
		mRoutes.clear();

		SecuredStorage securedStorage = SecuredStorage.get(getActivity(), mAccount);
		Dao<Route, Integer> routeDao = securedStorage.getRouteDao();
		QueryBuilder<Route, Integer> queryBuilder = routeDao.queryBuilder();
		
		try {
			mRoutes.addAll(queryBuilder.where().eq("psr", mPsr.getId()).and().eq("visit_date", date).query());  
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
		
		mRouteAdapter.notifyDataSetChanged();
	}
	
	public static class RouteViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView title;
		protected TextView subtitle;
		protected TextView description;
		
		public RouteViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			description = (TextView) itemView.findViewById(R.id.description);
		}
		
	}

	
}
