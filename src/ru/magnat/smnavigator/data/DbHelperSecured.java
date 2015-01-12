package ru.magnat.smnavigator.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.javaprotrepticon.android.androidutils.Apps;

import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Georegion;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.StoreProperty;
import ru.magnat.smnavigator.model.Target;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;

public class DbHelperSecured {
	
	private static final String TAG = "DB_HELPER_SECURED";
	
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static DbHelperSecured sInstance;
	
	private JdbcPooledConnectionSource mConnectionSource;
	 
	private DbHelperSecured(Context context, Account account) {
		AccountManager accountManager = AccountManager.get(context); 
		
		String accountName = account.name;
		String accountPassword = accountManager.getPassword(account);
		
		String accountFolder = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + accountName + "/";
		String accountStorage = accountFolder + context.getPackageName() + "-" + Apps.getVersionName(context);
		
		File accountDirectory = new File(accountFolder);
		
		if (!accountDirectory.exists()) {
			accountDirectory.mkdirs();
		}
		
		if (!new File(accountFolder + "initial-script.sql").exists()) {
			copyInitialScripts(context, accountFolder);
		}
		
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:h2:file:");
		urlBuilder.append(accountStorage + ";");
		urlBuilder.append("database_to_upper=false;");
		urlBuilder.append("file_lock=file;");
		urlBuilder.append("ifexists=false;");
		urlBuilder.append("ignorecase=true;");
		urlBuilder.append("page_size=1024;");
		urlBuilder.append("cache_size=1024;");
		urlBuilder.append("autocommit=on;");
		urlBuilder.append("compress=false;");
		urlBuilder.append("cipher=aes;");
		urlBuilder.append("user=" + accountName + ";");
		urlBuilder.append("password=" + accountPassword + " " + accountPassword + ";");
		
		if (!new File(accountStorage + ".h2.db").exists()) {	
			urlBuilder.append("init=runscript from '" + accountFolder + "initial-script.sql" + "';");
		} else {
			urlBuilder.append("init=set schema smnavigator;");
		}
		
		String url = urlBuilder.toString();
		
		try {
			mConnectionSource = new JdbcPooledConnectionSource(url, accountName, accountPassword + " " + accountPassword); 
			
			mManagerDao = DaoManager.createDao(mConnectionSource, Manager.class);
			mBranchDao = DaoManager.createDao(mConnectionSource, Branch.class);
			mDepartmentDao = DaoManager.createDao(mConnectionSource, Department.class);
			mPsrDao = DaoManager.createDao(mConnectionSource, Psr.class);
			mRouteDao = DaoManager.createDao(mConnectionSource, Route.class);
			mStoreDao = DaoManager.createDao(mConnectionSource, Store.class);
			mStorePropertyDao = DaoManager.createDao(mConnectionSource, StoreProperty.class);
			mCustomerDao = DaoManager.createDao(mConnectionSource, Customer.class);
			mMeasureDao = DaoManager.createDao(mConnectionSource, Measure.class);
			mTargetDao = DaoManager.createDao(mConnectionSource, Target.class);
			mGeoregionDao = DaoManager.createDao(mConnectionSource, Georegion.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void copyInitialScripts(Context context, String accountFolder) {
		try {
			InputStream is = context.getAssets().open("initial-scripts/initial-script.sql");
			OutputStream os = new FileOutputStream(accountFolder + "initial-script.sql"); 
			
			byte[] buffer = new byte[1024];
			int count = -1;
			
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Dao<Manager, String> mManagerDao;
	private Dao<Branch, String> mBranchDao;
	private Dao<Department, String> mDepartmentDao;
	private Dao<Psr, String> mPsrDao;
	private Dao<Route, String> mRouteDao;
	private Dao<Store, String> mStoreDao;
	private Dao<StoreProperty, String> mStorePropertyDao;
	private Dao<Customer, String> mCustomerDao;
	private Dao<Measure, String> mMeasureDao;
	private Dao<Target, String> mTargetDao;
	private Dao<Georegion, String> mGeoregionDao;
	
	public synchronized static DbHelperSecured get(Context context, Account account) {
		if (sInstance == null) {
			sInstance = new DbHelperSecured(context, account);
		}
		
		Log.d(TAG, "storage:instantiate->ok");
		
		return sInstance;
	}
	
	public synchronized static void close() {
		if (sInstance != null) {
			try {
				sInstance.mConnectionSource.close();
				sInstance = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Dao<Manager, String> getManagerDao() {
		return mManagerDao;
	}
	
	public Dao<Branch, String> getBranchDao() {
		return mBranchDao;
	}
	
	public Dao<Department, String> getDepartmentDao() {
		return mDepartmentDao;
	}
	
	public Dao<Store, String> getStoreDao() {
		return mStoreDao;
	}

	public Dao<StoreProperty, String> getStorePropertyDao() {
		return mStorePropertyDao;
	}
	
	public Dao<Psr, String> getPsrDao() {
		return mPsrDao;
	}

	public Dao<Route, String> getRouteDao() {
		return mRouteDao;
	}
	
	public Dao<Target, String> getTargetDao() {
		return mTargetDao;
	}
	
	public Dao<Georegion, String> getGeoregionDao() {
		return mGeoregionDao;
	}

	public Dao<Customer, String> getCustomerDao() {
		return mCustomerDao; 
	}

	public Dao<Measure, String> getMeasureDao() {
		return mMeasureDao;
	}
	
}
