package ru.magnat.smnavigator;

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
