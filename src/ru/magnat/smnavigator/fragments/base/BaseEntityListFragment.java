package ru.magnat.smnavigator.fragments.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.widgetutils.sync.SyncObserver;
import org.javaprotrepticon.android.widgetutils.sync.SyncStatus;
import org.javaprotrepticon.android.widgetutils.widget.StaticMapView;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

public abstract class BaseEntityListFragment<T> extends Fragment implements SyncObserver, OnRefreshListener {
	
	protected Account mAccount;
	
	protected Typeface mRobotoCondensedLight;
	protected Typeface mRobotoCondensedBold;
	
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<?> mRecyclerViewAdapter;
	
	private SwipeRefreshLayout mSwipeRefreshWidget;
	
	protected List<T> mEntityList = new ArrayList<T>();
	
	private Handler mHandler = new Handler();
	
    private final Runnable mRefreshDone = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(false);
        }

    };
    
    private final Runnable mRefreshBegin = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(true);
        }

    };
	
    protected Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU"));
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recyclerview, container, false);
	}
	
	@Override
	public void onRefresh() {
		refreshData();
	}
	
	protected abstract RecyclerView.Adapter<?> createAdapter();
	protected abstract void refreshData();
	
	public abstract class DefaultAdapter extends RecyclerView.Adapter<DefaultViewHolder> {
		
		@Override
		public int getItemCount() {
			return mEntityList.size();
		}
		
		@Override
		public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item_cardview, parent, false));
		}
		
	}
	
    public abstract class DataLoader extends AsyncTask<Void, Void, Void> {
    	
    	protected SecuredStorage mSecuredStorage;
    	protected QueryBuilder<T, Integer> mQueryBuilder;
    	
    	@Override
    	protected void onPreExecute() {
    		mIsLoading = true;
    		
    		mHandler.removeCallbacks(mRefreshDone);
            mHandler.removeCallbacks(mRefreshBegin);
            mHandler.postDelayed(mRefreshBegin, 250);
    		
    		mEntityList.clear();
    		mRecyclerViewAdapter.notifyDataSetChanged();
    		
    		mSecuredStorage = new SecuredStorage(getActivity(), mAccount);
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSecuredStorage.closeConnection();
			
			mRecyclerViewAdapter.notifyDataSetChanged();
			
			mHandler.removeCallbacks(mRefreshBegin);
            mHandler.removeCallbacks(mRefreshDone);
            mHandler.postDelayed(mRefreshDone, 10);
			
			mIsLoading = false;
		}
    	
    }
    
    protected String mQueryText = "%%";
    
    private boolean mIsLoading;
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
				
		inflater.inflate(R.menu.base_entity_list_fragment, menu);
		
		MenuItem menuItem = menu.findItem(R.id.actionSearch2);
		menuItem.setIcon(getResources().getDrawable(R.drawable.search));
		 
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch2));
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
	    	private boolean makeQuery(String query) {
	    		boolean result = false;
	    		
				if (query.length() >= 3) {
					mQueryText = "%" + query + "%";
					
					refreshData();
					
					result = true;
				}
				if (query.isEmpty()) {
					mQueryText = "%%";
					
					refreshData();
					
					result = true;
				}
				
				return result;
	    	}
	    	
			@Override
			public boolean onQueryTextSubmit(String query) {
				return mIsLoading ? false : makeQuery(query);
			}
			
			@Override
			public boolean onQueryTextChange(String query) {
				return mIsLoading ? false : makeQuery(query);
			}
		});
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	private int columns() {
		int columns = 2;
		
		Configuration config = getResources().getConfiguration();
		
		int screenSize = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 3;
		    } else {
		    	columns = 2;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 4;
		    } else {
		    	columns = 2;
		    }
		} 
		
		return columns;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		
		mRobotoCondensedLight = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Light");
		mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns());
        layoutManager.setReverseLayout(false);
		
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setVerticalScrollBarEnabled(true); 
        
		mRecyclerViewAdapter = createAdapter();
		mRecyclerView.setAdapter(mRecyclerViewAdapter); 
        
        mSwipeRefreshWidget = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this); 
        
        refreshData();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity) getActivity()).registerSyncObserver(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		((MainActivity) getActivity()).unregisterSyncObserver(this);
	}
	
	@Override
	public void onStatusChanged(SyncStatus status) {
		switch (status) {
			case STARTED: {
				
			} break;
			case ACK: {
				
			} break;
			case COMPLETED: {
				refreshData();
			} break;
			case CANCELED: {
				
			} break;
			case ERROR: {
				
			} break;
			default: {
				throw new RuntimeException();
			}
		}
	}
	
	public static class DefaultViewHolder extends RecyclerView.ViewHolder {
		
		public StaticMapView staticmap;
		
		public TextView title;
		public TextView subtitle;
		public TextView description;
		public TextView staticmaptitle;
		
		public DefaultViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			description = (TextView) itemView.findViewById(R.id.description);
			staticmap = (StaticMapView) itemView.findViewById(R.id.staticmap); 
			staticmaptitle = (TextView) itemView.findViewById(R.id.staticmaptitle); 
		}
		
	}

}
