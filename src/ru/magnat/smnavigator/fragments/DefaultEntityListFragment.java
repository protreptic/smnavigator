package ru.magnat.smnavigator.fragments;

import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.widgetutils.R;
import org.javaprotrepticon.android.widgetutils.fragment.base.BaseFragment;
import org.javaprotrepticon.android.widgetutils.widget.StaticMapView;

import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.sync.util.SyncObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

public abstract class DefaultEntityListFragment<T> extends BaseFragment implements SyncObserver {
	
	protected List<T> mEntityList = new ArrayList<T>();
	
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
	
    public abstract class SecuredStorageDataLoader extends BaseDataLoader {
    	
    	protected SecuredStorage mSecuredStorage;
    	protected QueryBuilder<T, Integer> mQueryBuilder;
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		mEntityList.clear();
    		mSecuredStorage = new SecuredStorage(getActivity(), mAccount);
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {		
			mSecuredStorage.closeConnection();
			
			super.onPostExecute(result); 
		}
    	
    }
	
}
