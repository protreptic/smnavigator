package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.base.BaseEntityListFragment;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class PsrListFragment extends BaseEntityListFragment {

	private String mQueryText = "%%";
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.psr_fragment, menu);
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch));
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
		
		mRecyclerViewAdapter = new PsrAdapter();
		
		mRecyclerView.setAdapter(mRecyclerViewAdapter); 
		
		new LoadData().execute();
	}
	
	private List<Psr> mPsrs = new ArrayList<Psr>();
	
	public class PsrAdapter extends RecyclerView.Adapter<PsrViewHolder> {
		
		private Typeface mRobotoCondensedBold;
		
		public PsrAdapter() {
			mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		}
		
		@Override
		public int getItemCount() {
			return mPsrs.size();
		}

		@Override
		public void onBindViewHolder(PsrViewHolder holder, int position) {
			final Psr psr = mPsrs.get(position);
			
			holder.title.setText(psr.getName());
			holder.title.setTypeface(mRobotoCondensedBold);
			
			holder.subtitle.setText(psr.getBranch().getName());
			holder.subtitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
			holder.description.setText(psr.getDepartment().getName()); 
			holder.description.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
			holder.staticmap.setMappable(psr); 
			
			holder.staticmaptitle.setText(Text.prepareAddress(psr.getProject()));
			holder.staticmaptitle.setTypeface(Fonts.get(getActivity()).getDefaultTypeface());
			
			holder.itemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Bundle arguments = new Bundle();
					arguments.putParcelable("account", mAccount);
					arguments.putInt("psr_id", psr.getId());   
					
					Fragment fragment = new PsrFragment();
					fragment.setArguments(arguments); 
					
		            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		            fragmentTransaction.replace(R.id.content_frame, fragment);
		            fragmentTransaction.addToBackStack(null);
		            fragmentTransaction.commit();	
				}
			});
		}

		@Override
		public PsrViewHolder onCreateViewHolder(ViewGroup parent, final int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item_cardview, parent, false);
			
			return new PsrViewHolder(itemView);
		}
		
	}
	
	public class LoadData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mPsrs.clear();

			SecuredStorage securedStorage = SecuredStorage.get(getActivity(), mAccount);
			
			Dao<Psr, Integer> psrDao = securedStorage.getPsrDao();
			
			QueryBuilder<Psr, Integer> queryBuilder;
			
			try {
				queryBuilder = psrDao.queryBuilder();
				queryBuilder.where()
					.like("name", mQueryText)
						.or()
					.like("project", mQueryText);
				queryBuilder.orderBy("name", true);
				
				mPsrs.addAll(queryBuilder.query());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			SecuredStorage.close();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mRecyclerViewAdapter.notifyDataSetChanged();
		}
		
	}
	
	public static class PsrViewHolder extends RecyclerView.ViewHolder {

		protected StaticMapView staticmap;
		
		protected TextView title;
		protected TextView subtitle;
		protected TextView description;
		protected TextView staticmaptitle;
		
		public PsrViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			description = (TextView) itemView.findViewById(R.id.description);
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
		}
	}
	
}
