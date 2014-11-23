package ru.magnat.smnavigator;

import ru.magnat.smnavigator.data.db.MainDbHelper;

public class Application extends android.app.Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		MainDbHelper.getInstance(getBaseContext());
	}
	
}
