package ru.magnat.smnavigator.data.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import ru.magnat.smnavigator.entities.Psr;
import ru.magnat.smnavigator.entities.PsrRoute;
import ru.magnat.smnavigator.entities.Store;
import ru.magnat.smnavigator.entities.StoreStatistics;
import ru.magnat.smnavigator.util.Apps;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

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
	
	private ConnectionSource mConnectionSource;
	
	private MainDbHelper(Context context) {
		mContext = context;
		
		DB_PATH = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/";
		DB_NAME = context.getPackageName() + "-" + Apps.getVersionName(context);
		DB_FULL_NAME = DB_PATH + DB_NAME;
		DB_URL = "jdbc:h2:file:" + DB_FULL_NAME + ";file_lock=no;ifexists=true;ignorecase=true;page_size=1024;cache_size=8192;autocommit=on;init=set schema sm_navigator";
		
		initDb();
	}
	
	public static MainDbHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MainDbHelper(context);
		}
		
		return sInstance;
	}
	
	public void deployDb() throws IOException {
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
	private Dao<PsrRoute, String> mPsrRouteDao;
	
	private void initDb() {
		try {
			if (!new File(DB_FULL_NAME + ".h2.db").exists()) {
				deployDb();
			}

			mConnectionSource = new JdbcConnectionSource(DB_URL);
			
			mStoreDao = DaoManager.createDao(mConnectionSource, Store.class);
			mStoreStatisticsDao = DaoManager.createDao(mConnectionSource, StoreStatistics.class);
			mPsrDao = DaoManager.createDao(mConnectionSource, Psr.class);
			mPsrRouteDao = DaoManager.createDao(mConnectionSource, PsrRoute.class);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ConnectionSource getConnectionSource() {
		return mConnectionSource;
	}
	
	public void release() {
		try {
			if (mConnectionSource != null) {
		        mConnectionSource.close();
		        mConnectionSource = null;
			}
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

	public Dao<PsrRoute, String> getPsrRouteDao() {
		return mPsrRouteDao;
	}
	
}
