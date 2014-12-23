package ru.magnat.smnavigator.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.h2.engine.DbSettings;

import ru.magnat.smnavigator.map.geofence.Geofenceable;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.util.Apps;
import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;

public class DbHelper {
	
	private static final String TAG = "DB_HELPER";
	
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static DbHelper sInstance;
	
	private Context mContext;
	
	private static String DB_PATH;
	private static String DB_NAME;
	private static String DB_FULL_NAME;
	private static String DB_URL;
	
	private JdbcPooledConnectionSource mConnectionSource;
	 
	private DbHelper(Context context, Account account) {
		mContext = context;
		
		SharedPreferences sharedPreferences = context.getSharedPreferences("global.storage", 0);
		
		// 
		boolean databaseToUpper = sharedPreferences.getBoolean("global.storage.databaseToUpper", false);
		boolean ifExists = sharedPreferences.getBoolean("global.storage.ifExists", true);
		boolean ignoreCase = sharedPreferences.getBoolean("global.storage.ignoreCase", true);
		boolean autoCommit = sharedPreferences.getBoolean("global.storage.autoCommit", true);
		boolean compress = sharedPreferences.getBoolean("global.storage.compress", false);
		
		String fileLock = sharedPreferences.getString("global.storage.fileLock", "no");
		DbSettings.getInstance(null);
		int pageSize = sharedPreferences.getInt("global.storage.pageSize", 1024);
		int cacheSize = sharedPreferences.getInt("global.storage.cacheSize", 1024);
		
		DB_PATH = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + account.name + "/";
		DB_NAME = context.getPackageName() + "-" + Apps.getVersionName(context);
		DB_FULL_NAME = DB_PATH + DB_NAME;
		DB_URL = "jdbc:h2:file:" + DB_FULL_NAME + ";database_to_upper=false;file_lock=no;ifexists=true;ignorecase=true;page_size=1024;cache_size=1024;autocommit=on;init=set schema sm_navigator;";
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("jdbc:h2:file:");
		stringBuilder.append(DB_FULL_NAME + ";");
		stringBuilder.append("database_to_upper=no;");
		stringBuilder.append("file_lock=no;");
		stringBuilder.append("ifexists=true;");
		stringBuilder.append("ignorecase=true;");
		stringBuilder.append("page_size=1024;");
		stringBuilder.append("cache_size=1024;");
		stringBuilder.append("autocommit=on;");
		
		Log.d(TAG, "storage:instantiate->" + DB_FULL_NAME);
		
		initDb();
	}
	
	public synchronized static DbHelper getInstance(Context context, Account account) {
		if (sInstance == null) {
			sInstance = new DbHelper(context, account);
		}
		
		Log.d(TAG, "storage:instantiate->ok");
		
		return sInstance;
	}
	
	public void deployDb() throws IOException {
		Log.d(TAG, "storage:deploy->" + DB_FULL_NAME);
		
		File accountDirectory = new File(DB_PATH);
		
		if (!accountDirectory.exists()) {
			accountDirectory.mkdirs();
		}
		
		InputStream is = mContext.getAssets().open("storage.h2.db");
		OutputStream os = new FileOutputStream(DB_FULL_NAME + ".h2.db"); 
		byte[] buffer = new byte[1024];
		int count;
		while ((count = is.read(buffer)) > 0) {
			os.write(buffer, 0, count);
		}
		os.close();
		is.close();
		
		Log.d(TAG, "storage:deploy->ok");
	}
	
	private Dao<Manager, String> mManagerDao;
	private Dao<Branch, String> mBranchDao;
	private Dao<Department, String> mDepartmentDao;
	private Dao<Psr, String> mPsrDao;
	private Dao<Route, String> mRouteDao;
	private Dao<Store, String> mStoreDao;
	private Dao<Customer, String> mCustomerDao;
	private Dao<Measure, String> mMeasureDao;
	private Dao<Geofenceable, String> mGeoregionDao;
	
	private void initDb() {
		Log.d(TAG, "storage:init->" + DB_FULL_NAME); 
		
		try {
			if (!new File(DB_FULL_NAME + ".h2.db").exists()) {				
				deployDb();
			}
			
			mConnectionSource = new JdbcPooledConnectionSource(DB_URL); 
			
			mManagerDao = DaoManager.createDao(mConnectionSource, Manager.class);
			mBranchDao = DaoManager.createDao(mConnectionSource, Branch.class);
			mDepartmentDao = DaoManager.createDao(mConnectionSource, Department.class);
			mPsrDao = DaoManager.createDao(mConnectionSource, Psr.class);
			mRouteDao = DaoManager.createDao(mConnectionSource, Route.class);
			mStoreDao = DaoManager.createDao(mConnectionSource, Store.class);
			mCustomerDao = DaoManager.createDao(mConnectionSource, Customer.class);
			mMeasureDao = DaoManager.createDao(mConnectionSource, Measure.class);
						
			mGeoregionDao = DaoManager.createDao(mConnectionSource, Geofenceable.class);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "storage:init->ok"); 
	}

	public static void close() {
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

	public Dao<Measure, String> getMeasureDao() {
		return mMeasureDao;
	}
	
	public Dao<Psr, String> getPsrDao() {
		return mPsrDao;
	}

	public Dao<Route, String> getRouteDao() {
		return mRouteDao;
	}
	
	public Dao<Geofenceable, String> getGeoregionDao() {
		return mGeoregionDao;
	}

	public Dao<Customer, String> getCustomerDao() {
		return mCustomerDao; 
	}
	
}
