package ru.magnat.smnavigator.view;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Manager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoView extends RelativeLayout {

	public UserInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);

		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.user_info_view, this, false);
		
		TextView name = (TextView) relativeLayout.findViewById(R.id.title); 
		name.setTypeface(Fonts.get(context).getTypeface("RobotoCondensed-Regular"));  
		
		TextView branch = (TextView) relativeLayout.findViewById(R.id.subtitle); 
		branch.setTypeface(Fonts.get(context).getTypeface("RobotoCondensed-Regular"));  
		
		addView(relativeLayout); 
	}

	public void setManager(Manager manager) {
		TextView name = (TextView) findViewById(R.id.title); 
		name.setText(manager.getName());   
		
		TextView branch = (TextView) findViewById(R.id.subtitle); 
		branch.setText(manager.getBranch().getName()); 
	}
	
}
