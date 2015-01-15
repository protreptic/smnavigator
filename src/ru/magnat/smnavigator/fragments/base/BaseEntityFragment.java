package ru.magnat.smnavigator.fragments.base;

import org.javaprotrepticon.android.androidutils.Fonts;

import ru.magnat.smnavigator.R;
import android.accounts.Account;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseEntityFragment extends Fragment {

	protected Account mAccount;
	
	protected Typeface mRobotoCondensedLight;
	protected Typeface mRobotoCondensedBold;
	
	protected FragmentTabHost mFragmentTabHost;
	
	protected CardView mEntityCard;
	
	public BaseEntityFragment() {
		mRobotoCondensedLight = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Light");
		mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
	}
	
	protected View getTabIndicator(String title) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null, false);
		
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setTypeface(mRobotoCondensedBold); 
        tv.setText(title);
        
        return view;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mFragmentTabHost = (FragmentTabHost) inflater.inflate(R.layout.entity_fragment, container, false);
		mFragmentTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		
		mEntityCard = (CardView) mFragmentTabHost.findViewById(R.id.card_view);
		
		return mFragmentTabHost;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAccount = getArguments().getParcelable("account");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mFragmentTabHost = null;
	}
	
}
