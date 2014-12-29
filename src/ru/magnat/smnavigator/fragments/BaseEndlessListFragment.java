package ru.magnat.smnavigator.fragments;

import android.widget.ProgressBar;

public class BaseEndlessListFragment extends BaseListFragment {
	
	protected boolean mIsLoading;
	protected long offset;
	protected long count;
	protected long limit = 25; 
	
	protected ProgressBar progressBar;
	
}
