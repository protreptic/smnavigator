package ru.magnat.smnavigator.storage;

import java.sql.SQLException;

import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Geocoordinate;
import ru.magnat.smnavigator.model.Georegion;
import ru.magnat.smnavigator.model.Location;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.StoreProperty;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.model.Track;
import android.accounts.Account;
import android.content.Context;

import org.javaprotrepticon.android.widgetutils.storage.BaseSecuredStorage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class SecuredStorage extends BaseSecuredStorage {
	
	public SecuredStorage(Context context, Account account) {
		super(context, account);
		
		try {
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
			mLocationDao = DaoManager.createDao(mConnectionSource, Location.class);
			mGeocoordinateDao = DaoManager.createDao(mConnectionSource, Geocoordinate.class);
			mTrackDao = DaoManager.createDao(mConnectionSource, Track.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Dao<Manager, Integer> mManagerDao;
	private Dao<Branch, Integer> mBranchDao;
	private Dao<Department, Integer> mDepartmentDao;
	private Dao<Psr, Integer> mPsrDao;
	private Dao<Route, Integer> mRouteDao;
	private Dao<Store, Integer> mStoreDao;
	private Dao<StoreProperty, Integer> mStorePropertyDao;
	private Dao<Customer, Integer> mCustomerDao;
	private Dao<Measure, Integer> mMeasureDao;
	private Dao<Target, Integer> mTargetDao;
	private Dao<Georegion, Integer> mGeoregionDao;
	private Dao<Location, Integer> mLocationDao;
	private Dao<Track, Integer> mTrackDao;
	private Dao<Geocoordinate, Integer> mGeocoordinateDao;
	
	public Dao<Location, Integer> getLocationDao() {
		return mLocationDao;
	}
	
	public Dao<Manager, Integer> getManagerDao() {
		return mManagerDao;
	}
	
	public Dao<Branch, Integer> getBranchDao() {
		return mBranchDao;
	}
	
	public Dao<Department, Integer> getDepartmentDao() {
		return mDepartmentDao;
	}
	
	public Dao<Store, Integer> getStoreDao() {
		return mStoreDao;
	}

	public Dao<StoreProperty, Integer> getStorePropertyDao() {
		return mStorePropertyDao;
	}
	
	public Dao<Psr, Integer> getPsrDao() {
		return mPsrDao;
	}

	public Dao<Route, Integer> getRouteDao() {
		return mRouteDao;
	}
	
	public Dao<Target, Integer> getTargetDao() {
		return mTargetDao;
	}
	
	public Dao<Georegion, Integer> getGeoregionDao() {
		return mGeoregionDao;
	}

	public Dao<Customer, Integer> getCustomerDao() {
		return mCustomerDao; 
	}

	public Dao<Measure, Integer> getMeasureDao() {
		return mMeasureDao;
	}

	public Dao<Track, Integer> getTrackDao() {
		return mTrackDao;
	}

	public Dao<Geocoordinate, Integer> getGeocoordinateDao() {
		return mGeocoordinateDao;
	}

}
