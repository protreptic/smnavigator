package ru.magnat.smnavigator.activities;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.fragments.NavigationListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ObjectsActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		
		getActionBar().setTitle(""); 
		getSupportFragmentManager().beginTransaction().add(R.id.conteiner, new NavigationListFragment()).commit();
	}
}
