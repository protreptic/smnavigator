package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.model.experimental.parcel.StoreParcel;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TargetFragment extends Fragment {
	
	private TargetAdapter mTargetAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recycler_view, container, false);
	}

	private Account mAccount;
    private Store mStore;
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		
		StoreParcel storeParcel = getArguments().getParcelable("store");
		mStore = storeParcel.getStore();
		
		mTargetAdapter = new TargetAdapter();
		
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setReverseLayout(false);
		
		RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(mTargetAdapter); 
		
		loadTargets();
	}
	
	public class TargetAdapter extends RecyclerView.Adapter<TargetViewHolder> {
		
		private Typeface mRobotoCondensedBold;
		
		public TargetAdapter() {
			mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		}
		
		@Override
		public int getItemCount() {
			return mTargets.size();
		}

		@Override
		public void onBindViewHolder(TargetViewHolder holder, int position) {
			Target target = mTargets.get(position);
			
			holder.kpiName.setTypeface(mRobotoCondensedBold);  
			holder.kpiName.setText(target.getName());
			
			holder.targetView.setTypeface(mRobotoCondensedBold);  
			holder.targetView.setText(target.getTarget().toString());
			
			holder.fact.setTypeface(mRobotoCondensedBold);  
			holder.fact.setText(target.getFact().toString());
			
			holder.index.setTypeface(mRobotoCondensedBold);  
			holder.index.setText(target.getIndex().toString());
		}

		@Override
		public TargetViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_layout, parent, false);
			
			return new TargetViewHolder(itemView);
		}
		
	}
	
	private List<Target> mTargets = new ArrayList<Target>();
	
	private void loadTargets() {
		mTargets.clear();

		SecuredStorage securedStorage = SecuredStorage.get(getActivity(), mAccount);
		
		try {
			mTargets.addAll(securedStorage.getTargetDao().queryForEq("store", mStore.getId()));  
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		SecuredStorage.close();
		
		mTargetAdapter.notifyDataSetChanged();
	}
	
	public static class TargetViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView kpiName;
		protected TextView targetView;
		protected TextView fact;
		protected TextView index;
		
		public TargetViewHolder(View itemView) {
			super(itemView);
			
			kpiName = (TextView) itemView.findViewById(R.id.kpiName); 
			targetView = (TextView) itemView.findViewById(R.id.target); 
			fact = (TextView) itemView.findViewById(R.id.fact); 
			index = (TextView) itemView.findViewById(R.id.index); 
		}
		
	}
	
}
