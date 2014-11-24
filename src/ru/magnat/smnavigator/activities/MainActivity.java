package ru.magnat.smnavigator.activities;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.R.drawable;
import ru.magnat.smnavigator.R.id;
import ru.magnat.smnavigator.R.layout;
import ru.magnat.smnavigator.fragments.NavigationCompositeFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		getActionBar().setTitle("");
		getActionBar().setIcon(getResources().getDrawable(R.drawable.logotype)); 
		 
		setContentView(R.layout.main_activity); 
		
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.conteiner, new NavigationCompositeFragment())
			.commit();
	}
	
}
