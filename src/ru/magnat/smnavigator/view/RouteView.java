package ru.magnat.smnavigator.view;

import java.text.SimpleDateFormat;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Route;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RouteView extends RelativeLayout {

	public RouteView(Context context, Route route) {
		super(context); 
		
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.visit_list_item, this, false);
		
		TextView title = (TextView) relativeLayout.findViewById(R.id.title); 
		title.setTypeface(Fonts.get(context).getDefaultTypeface());  
		title.setText(route.getStore().getCustomer().getName());    
		
		SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm");
		
		TextView subtitle = (TextView) relativeLayout.findViewById(R.id.subtitle); 
		subtitle.setTypeface(Fonts.get(context).getDefaultTypeface());  
		subtitle.setText(format.format(route.getVisitDate()));  
		
		TextView description = (TextView) relativeLayout.findViewById(R.id.description); 
		description.setTypeface(Fonts.get(context).getDefaultTypeface());  
		description.setText(Text.prepareAddress(route.getStore().getAddress())); 
		
		ImageView actions = (ImageView) relativeLayout.findViewById(R.id.actions); 
		actions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
		
		addView(relativeLayout); 
	}

}
