package ru.magnat.smnavigator.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;

public class BaseEndlessListFragment extends BaseListFragment implements OnScrollListener  {
	
	protected boolean mIsLoading;
	protected long offset;
	protected long count;
	protected long limit = 25; 
	
	protected ProgressBar progressBar;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		progressBar = new ProgressBar(getActivity());
		
		getExpandableListView().setOnScrollListener(this);
		getExpandableListView().addFooterView(progressBar); 		
 	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) { 
			if (getExpandableListView().getFirstVisiblePosition() == 0) {
			}
			
	        if (getExpandableListView().getLastVisiblePosition() >= getExpandableListView().getCount() - 1 && !mIsLoading) {
	        	if (count != offset) {
	        		//loadData();
	        	} else {
	        		if (progressBar != null) {
	        			progressBar.setVisibility(View.GONE); 
	        		}
	        	}
	        }
	    }
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
	
}
