package ru.magnat.smnavigator.view;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.util.Fonts;
import ru.magnat.smnavigator.util.Text;
import ru.magnat.smnavigator.widget.StaticMapView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StoreView extends RelativeLayout {

	public StoreView(final Context context, Store store) {
		super(context); 
		
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.default_list_item, this, false);
		
		TextView name = (TextView) relativeLayout.findViewById(R.id.title); 
		name.setTypeface(Fonts.get(context).getDefaultTypeface());  
		name.setText(store.getCustomer().getName());   
		
		TextView address = (TextView) relativeLayout.findViewById(R.id.description); 
		address.setTypeface(Fonts.get(context).getDefaultTypeface());  
		address.setText(Text.prepareAddress(store.getAddress())); 
		
		TextView channel = (TextView) relativeLayout.findViewById(R.id.staticmaptitle); 
		channel.setTypeface(Fonts.get(context).getDefaultTypeface());  
		channel.setText(store.getChannel()); 
		
		TextView goldenStatus = (TextView) relativeLayout.findViewById(R.id.subtitle); 
		goldenStatus.setTypeface(Fonts.get(context).getDefaultTypeface());  
		goldenStatus.setText(""); 
		
		StaticMapView staticMapView = (StaticMapView) relativeLayout.findViewById(R.id.staticmap); 
		staticMapView.setMappable(store); 
		
		ImageView details = (ImageView) relativeLayout.findViewById(R.id.actions); 
		details.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
		
		addView(relativeLayout); 
	}

}
