package ru.magnat.smnavigator.auth.account;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.util.Fonts;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountHelper {
	
	private static AccountHelper sInstance;

	private Context mContext;
	private AccountManager mAccountManager;
	
	private List<Account> mAccounts = new ArrayList<Account>();
	private Account mAccount;
	
	private AccountHelper() {}
	
	private AccountHelper(Context context) {
		mContext = context;
		mAccountManager = AccountManager.get(context);
		
		mAccounts = getAccounts();
	}

	public Account getCurrentAccount() {
		return mAccount;
	}
	
	public void setCurrentAccount(Account account) {
		mAccount = account;
	}
	
	public Account getAccount(int position) {
		return mAccounts.get(position);
	}
	
	public List<Account> getAccounts() {
		Account[] accounts = mAccountManager.getAccountsByType(AccountWrapper.ACCOUNT_TYPE);
		List<Account> result = new ArrayList<Account>();
		
		for (Account account : accounts) {
			result.add(account);
		}
		
		return result;
	}
	
	public AccountListAdapter getAccountListAdapter() {
		return new AccountListAdapter();
	}
	
	public synchronized static AccountHelper get(Context context) {
		if (sInstance == null) {
			sInstance = new AccountHelper(context);
		}
		
		return sInstance;
	}
	
	public synchronized static void release() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	public class AccountListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAccounts.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return mAccounts.get(position); 
		}

		@Override
		public long getItemId(int position) {
			if (position == mAccounts.size()) {
				return 6666;
			} else {
				return mAccounts.get(position).hashCode();
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout linearLayout = new LinearLayout(mContext);
			linearLayout.setPadding(15, 10, 15, 10); 
			linearLayout.setOrientation(LinearLayout.VERTICAL); 
			
			TextView textView1 = new TextView(mContext);
			textView1.setTypeface(Fonts.getInstance(mContext).getTypeface("RobotoCondensed-Regular"));
			textView1.setTextSize(32); 
			
			
			TextView textView2 = new TextView(mContext);
			textView2.setTypeface(Fonts.getInstance(mContext).getDefaultTypeface()); 
			textView2.setTextSize(18); 
			
			if (position == mAccounts.size()) {
				textView1.setText(mContext.getString(R.string.addUser)); 
				textView2.setText(mContext.getString(R.string.addUserDescription)); 
			} else {
				textView1.setText(mAccounts.get(position).name); 
				textView2.setText(mAccounts.get(position).type); 
			}
			
			linearLayout.addView(textView1);
			linearLayout.addView(textView2);
			
			return linearLayout;
		}
		
	}

	public void refresh() {
		mAccounts = getAccounts();
	}
	
}
