package ru.magnat.smnavigator.view;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.util.Fonts;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StoreStatisticsView extends RelativeLayout {

	private TextView title;
	private TextView subTitle;
	
	public StoreStatisticsView(Context context) {
		super(context);
		
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.store_statistics_view, this, false);
		
		title = (TextView) relativeLayout.findViewById(R.id.title); 
		title.setTypeface(Fonts.getInstance(context).getDefaultTypeface());  
		
		subTitle = (TextView) relativeLayout.findViewById(R.id.description); 
		subTitle.setTypeface(Fonts.getInstance(context).getDefaultTypeface());  
		
		addView(relativeLayout); 
	}
	
	public void setTitle(String title) {
		this.title.setText(title);   
	}
	
	public void setSubTitle(String subTitle) {
		this.subTitle.setText(subTitle);
	}

}
