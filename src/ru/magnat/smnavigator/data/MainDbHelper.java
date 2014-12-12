package ru.magnat.smnavigator.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import ru.magnat.smnavigator.account.AccountHelper;
import ru.magnat.smnavigator.map.geofence.Geofenceable;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.StoreStatistics;
import ru.magnat.smnavigator.util.Apps;
import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;

public class MainDbHelper {
	
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static MainDbHelper sInstance;
	
	private Context mContext;
	
	private static String DB_PATH;
	private static String DB_NAME;
	private static String DB_FULL_NAME;
	private static String DB_URL;
	
//	private static final boolean DB_OPTION_FILE_LOCK = true;
//	private static final boolean DB_OPTION_IGNORECASE = true;
//	private static final long DB_OPTION_PAGE_SIZE = 1024;
//	private static final long DB_OPTION_CACHE_SIZE = 8192;
	
	private JdbcPooledConnectionSource mConnectionSource;
	 
	private MainDbHelper(Context context) {
		mContext = context;
		
		AccountHelper accountHelper = AccountHelper.getInstance(context);
		
		Account account = accountHelper.getCurrentAccount();
		
		DB_PATH = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + account.name + "/";
		DB_NAME = context.getPackageName() + "-" + Apps.getVersionName(context);
		DB_FULL_NAME = DB_PATH + DB_NAME;
		DB_URL = "jdbc:h2:file:" + DB_FULL_NAME + ";file_lock=no;ifexists=true;ignorecase=true;page_size=1024;cache_size=1024;autocommit=on;init=set schema sm_navigator";
		
		initDb();
	}
	
	private MainDbHelper(Context context, Account account) {
		DB_PATH = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + account.name + "/";
		DB_NAME = context.getPackageName() + "-" + Apps.getVersionName(context);
		DB_FULL_NAME = DB_PATH + DB_NAME;
		DB_URL = "jdbc:h2:file:" + DB_FULL_NAME + ";file_lock=no;ifexists=true;ignorecase=true;page_size=1024;cache_size=1024;autocommit=on;init=set schema sm_navigator";
		
		initDb();
	}
	
	public static MainDbHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MainDbHelper(context);
		}
		
		return sInstance;
	}
	
	public static MainDbHelper getInstance(Context context, Account account) {
		if (sInstance == null) {
			sInstance = new MainDbHelper(context, account);
		}
		
		return sInstance;
	}
	
	public void deployDb() throws IOException {
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
	}
	
	private Dao<Store, String> mStoreDao;
	private Dao<StoreStatistics, String> mStoreStatisticsDao;
	private Dao<Psr, String> mPsrDao;
	private Dao<Route, String> mRouteDao;
	private Dao<Geofenceable, String> mGeoregionDao;
	
	private void initDb() {
		try {
			if (!new File(DB_FULL_NAME + ".h2.db").exists()) {
				deployDb();
			}
			
			mConnectionSource = new JdbcPooledConnectionSource(DB_URL); 
			
			mStoreDao = DaoManager.createDao(mConnectionSource, Store.class);
			mStoreStatisticsDao = DaoManager.createDao(mConnectionSource, StoreStatistics.class);
			mPsrDao = DaoManager.createDao(mConnectionSource, Psr.class);
			mRouteDao = DaoManager.createDao(mConnectionSource, Route.class);
			mGeoregionDao = DaoManager.createDao(mConnectionSource, Geofenceable.class);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void log() {
		try {
			Log.d("SQL LOG", "count of stores " + mStoreDao.countOf());
			Log.d("SQL LOG", "count of stores statistics " + mStoreStatisticsDao.countOf());
			Log.d("SQL LOG", "count of psrs " + mPsrDao.countOf());
			Log.d("SQL LOG", "count of routes " + mRouteDao.countOf());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Dao<Store, String> getStoreDao() {
		return mStoreDao;
	}

	public Dao<StoreStatistics, String> getStoreStatisticsDao() {
		return mStoreStatisticsDao;
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
	
}
