package ru.magnat.smnavigator.fragments.experimental;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.accounts.Account;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class TrackFragment extends Fragment {

	private Account mAccount;
	
	private RecyclerView mRecyclerView;
	private PsrAdapter 	mAdapter;
	private static Typeface sTypeface;
	private static Typeface sTypefaceBold;
		
	private String mQueryText = "%%";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}

    private GridLayoutManager createLayoutManager() {
        GridLayoutManager lm = new GridLayoutManager(getActivity(), 2);
        lm.setReverseLayout(false);
        lm.setSpanSizeLookup(mSpanSizeLookup);
        return lm;
    }
	
    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            Integer item = mAdapter.getValueAt(position);
            return 1 + (Math.abs(item.hashCode()) % mLayoutManager.getSpanCount());
        }
    };
    
    private GridLayoutManager mLayoutManager;
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sTypeface = Fonts.get(getActivity()).getDefaultTypeface();
		sTypefaceBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		
		mAccount = getArguments().getParcelable("account");
		
		mAdapter = new PsrAdapter();
		
		mLayoutManager = createLayoutManager();
		
		mRecyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mAdapter); 
		
		new LoadData().execute();
	}
	
	private List<Psr> mPsrs = new ArrayList<Psr>();
	
	public class PsrAdapter extends RecyclerView.Adapter<PsrViewHolder> {
		
		public int getValueAt(int position) {
			return mPsrs.get(position).hashCode();
		}
		
		@Override
		public int getItemCount() {
			return mPsrs.size();
		}

		@Override
		public void onBindViewHolder(PsrViewHolder holder, int position) {
			Psr psr = mPsrs.get(position);
			
			holder.title.setText(psr.getName());
			holder.subtitle.setText(psr.getBranch().getName());
			holder.description.setText(psr.getDepartment().getName()); 
			holder.staticmap.setMappable(psr); 
			holder.staticmaptitle.setText(Text.prepareAddress(psr.getProject()));
		}

		@Override
		public PsrViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
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
			
			try {
				QueryBuilder<Psr, Integer> queryBuilder = psrDao.queryBuilder();
				queryBuilder.where()
					.like("name", mQueryText)
						.or()
					.like("project", mQueryText);
				queryBuilder.orderBy("name", true);
				
				List<Psr> psrs = queryBuilder.query();
				
				mPsrs.addAll(psrs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			SecuredStorage.close();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
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
			title.setTypeface(sTypefaceBold);  
			
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			subtitle.setTypeface(sTypeface);  
			
			description = (TextView) itemView.findViewById(R.id.description);
			description.setTypeface(sTypeface); 
			
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
			staticmaptitle.setTypeface(sTypeface); 
		}

	}

}
