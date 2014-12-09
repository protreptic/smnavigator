package ru.magnat.smnavigator.activities;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.PsrListFragment;
import ru.magnat.smnavigator.fragments.StoreListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class ObjectsActivity extends FragmentActivity {
		
	private FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_tab_host);
		
		getActionBar().setTitle(""); 
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype_small)); 
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.endless_list_background)); 
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("psrs").setIndicator(getResources().getString(R.string.titlePsrs), null), PsrListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("stores").setIndicator(getResources().getString(R.string.titleStores), null), StoreListFragment.class, null); 
	}
		
}
