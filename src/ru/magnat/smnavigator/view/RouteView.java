package ru.magnat.smnavigator.view;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.auth.account.AccountHelper;
import ru.magnat.smnavigator.data.MainDbHelper;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import android.accounts.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RouteView extends RelativeLayout {

	private Route mRoute;
	
	public RouteView(final Context context, Route route) {
		super(context); 
		
		mRoute = route;
		
		AccountHelper accountHelper = AccountHelper.getInstance(context);
		
		Account account = accountHelper.getCurrentAccount();
		
		Store store = null;
		MainDbHelper dbHelper = MainDbHelper.getInstance(context, account);
		Dao<Store, String> storeDao = dbHelper.getStoreDao();
		
		try {
			store = storeDao.queryForId(mRoute.getStore().toString()); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.visit_list_item, this, false);
		
		TextView name = (TextView) relativeLayout.findViewById(R.id.title); 
		name.setTypeface(Fonts.getInstance(context).getDefaultTypeface());  
		name.setText((store != null ? store.getCustomer() : "null"));   
		
		TextView subtitle = (TextView) relativeLayout.findViewById(R.id.subtitle); 
		subtitle.setTypeface(Fonts.getInstance(context).getDefaultTypeface());  
		subtitle.setText(mRoute.getVisitDate());  
		
		TextView description = (TextView) relativeLayout.findViewById(R.id.description); 
		description.setTypeface(Fonts.getInstance(context).getDefaultTypeface());  
		description.setText(Text.prepareAddress((store != null ? store.getAddress() : "null"))); 
		
		ImageView details = (ImageView) relativeLayout.findViewById(R.id.actions); 
		details.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
		
		addView(relativeLayout); 
	}

}
