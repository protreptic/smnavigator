package ru.magnat.smnavigator.fragments.base;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.data.DbHelperSecured;
import ru.magnat.smnavigator.widget.ExpandableListFragment;
import android.accounts.Account;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;

public class BaseListFragment extends ExpandableListFragment {
	
	private Account mAccount;
	protected BaseExpandableListAdapter mAdapter;
	protected String mQueryText = "%%";
	private DbHelperSecured mDbHelper;
	
	public Account getAccount() {
		return mAccount;
	}
	
	public DbHelperSecured getDbHelper() {
		return mDbHelper;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
		
		mDbHelper = DbHelperSecured.get(getActivity(), mAccount);
		
		getExpandableListView().setGroupIndicator(null); 
		getExpandableListView().setDivider(null); 
		getExpandableListView().setDividerHeight(-6);
		getExpandableListView().setBackgroundColor(getResources().getColor(R.color.gray)); 
		
		setEmptyText(getString(R.string.emptyList)); 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	public class BaseDataLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setListShown(false); 
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			 
			setListShown(true); 
		}
		
	}
}
