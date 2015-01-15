package ru.magnat.smnavigator.fragments;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityListFragment;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import android.app.DatePickerDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

public class RouteFragment extends BaseEntityListFragment<Route> {
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.route_fragment, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionCalendar: {
				mCalendar.setTimeInMillis(date == null ? initDate().getTime() : date.getTime()); 
				
				int year = mCalendar.get(Calendar.YEAR);
				int monthOfYear = mCalendar.get(Calendar.MONTH); 
				int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
				
				new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mCalendar.clear();
						mCalendar.set(year, monthOfYear, dayOfMonth); 
						
						date = new Date(mCalendar.getTimeInMillis());
						
						refreshData();
					}
				}, year, monthOfYear, dayOfMonth).show();
			} break;
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private Date date;
	
	private Date initDate() {
		mCalendar.setTimeInMillis(System.currentTimeMillis()); 
		
		int year = mCalendar.get(Calendar.YEAR);
		int monthOfYear = mCalendar.get(Calendar.MONTH); 
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		mCalendar.clear();
		mCalendar.set(year, monthOfYear, dayOfMonth); 
		
		return new Date(mCalendar.getTimeInMillis());
	}
	
	@Override
	protected void refreshData() {
		new DataLoader() {
			
		    private Psr getPsr(int id) {
		    	Psr psr = null;
		    	
				try {
					psr = mSecuredStorage.getPsrDao().queryForId(id);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	
		    	return psr;
		    }
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					mQueryBuilder = mSecuredStorage.getRouteDao().queryBuilder();
					mQueryBuilder.where()
						.eq("psr", getPsr(getArguments().getInt("psr_id")))
							.and()
						.eq("visit_date", date == null ? initDate() : date);
					
					mEntityList.addAll(mQueryBuilder.query());  
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute(); 
	}
    
	@Override
	protected Adapter<?> createAdapter() {
		return new RecyclerView.Adapter<RouteViewHolder>() {
			
			@Override
			public int getItemCount() {
				return mEntityList.size();
			}

			@Override
			public void onBindViewHolder(RouteViewHolder holder, int position) {
				Route route = mEntityList.get(position);
				
				holder.title.setTypeface(mRobotoCondensedBold);  
				holder.title.setText(route.getStore().getCustomer().getName());
				
				SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
				
				holder.subtitle.setTypeface(mRobotoCondensedBold);  
				holder.subtitle.setText(format.format(route.getVisitDate()));
				
				holder.description.setTypeface(mRobotoCondensedLight);  
				holder.description.setText(route.getStore().getAddress());
			}

			@Override
			public RouteViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
				View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout, parent, false);
				
				return new RouteViewHolder(itemView);
			}
			
		};
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
