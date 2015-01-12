package ru.magnat.smnavigator.view;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Target;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TargetView extends RelativeLayout {
	
	private TextView kpiName;
	private TextView targetView;
	private TextView fact;
	private TextView index;
	
	public TargetView(Context context, Target target) {
		super(context);
		
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.target_layout, this, false);
		
		kpiName = (TextView) relativeLayout.findViewById(R.id.kpiName); 
		kpiName.setTypeface(Fonts.get(context).getDefaultTypeface());  
		kpiName.setText(target.getName()); 
		
		targetView = (TextView) relativeLayout.findViewById(R.id.target); 
		targetView.setTypeface(Fonts.get(context).getDefaultTypeface());  
		targetView.setText(target.getTarget().toString());
		 
		fact = (TextView) relativeLayout.findViewById(R.id.fact); 
		fact.setTypeface(Fonts.get(context).getDefaultTypeface());  
		fact.setText(target.getFact().toString());
		
		index = (TextView) relativeLayout.findViewById(R.id.index); 
		index.setTypeface(Fonts.get(context).getDefaultTypeface());  
		index.setText(target.getIndex().toString());
		
		addView(relativeLayout); 
	}
	
}
