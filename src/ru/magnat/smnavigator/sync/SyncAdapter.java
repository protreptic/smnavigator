package ru.magnat.smnavigator.sync;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.auth.account.AccountWrapper;
import ru.magnat.smnavigator.data.DbHelper;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.json.BranchDeserializer;
import ru.magnat.smnavigator.model.json.BranchSerializer;
import ru.magnat.smnavigator.model.json.CustomerDeserializer;
import ru.magnat.smnavigator.model.json.CustomerSerializer;
import ru.magnat.smnavigator.model.json.DepartmentDeserializer;
import ru.magnat.smnavigator.model.json.DepartmentSerializer;
import ru.magnat.smnavigator.model.json.PsrDeserializer;
import ru.magnat.smnavigator.model.json.PsrSerializer;
import ru.magnat.smnavigator.model.json.StoreDeserializer;
import ru.magnat.smnavigator.model.json.StoreSerializer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.j256.ormlite.dao.Dao;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    
    @SuppressWarnings("unused")
	private ContentResolver mContentResolver;
    
    private DbHelper mMainDbHelper;
    private Account mAccount;
    private String mAuthToken;
    
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }
    
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        
        mContentResolver = context.getContentResolver();
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;    	
    	
    	sendNotification("started");
    	
    	mAuthToken = AccountManager.get(getContext()).peekAuthToken(account, AccountWrapper.ACCOUNT_TYPE);
    	
    	Timer timer = new Timer("askSender");
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ask");
			}
		}, 0, 1500);
    	
		try {
			TimeUnit.SECONDS.sleep(2);
		
			mMainDbHelper = DbHelper.getInstance(getContext(), account);
			
			getManager();
			getBranch();
			getDepartment();
			getPsr();
			getRoute();
			getStore();
			getCustomer();
			getMeasure();
			
			DbHelper.close();
			
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			e.printStackTrace();
			sendNotification("error");	
	    }
		
		timer.cancel();
		
		sendNotification("completed");
    }
    
    private void sendNotification(String action) {
    	Intent intentStarted = new Intent(MainActivity.ACTION_SYNC);
    	intentStarted.putExtra("action", action);
    	intentStarted.putExtra("account", mAccount.name);
    	
    	getContext().sendBroadcast(intentStarted);
    }
    
    private void getManager() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getManager?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchSerializer());
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentSerializer());
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Manager> managers = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Manager>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Manager, String> managerDao = mMainDbHelper.getManagerDao();
		managerDao.setObjectCache(false); 
		managerDao.delete(managerDao.queryForAll());
		
		for (Manager manager : managers) {
			managerDao.createOrUpdate(manager);
			
			Log.d("", manager.toString()); 
		}
    }
    
    private void getBranch() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getBranch?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Branch> branches = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Branch>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Branch, String> branchDao = mMainDbHelper.getBranchDao();
		branchDao.setObjectCache(false); 
		branchDao.delete(branchDao.queryForAll());
		
		for (Branch branch : branches) {
			branchDao.createOrUpdate(branch);
			
			Log.d("", branch.toString()); 
		}
    }
    
    private void getDepartment() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getDepartment?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Department> departments = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Department>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Department, String> departmentDao = mMainDbHelper.getDepartmentDao();
		departmentDao.setObjectCache(false); 
		departmentDao.delete(departmentDao.queryForAll());
		
		for (Department department : departments) {
			departmentDao.createOrUpdate(department);
			
			Log.d("", department.toString()); 
		}
    }
    
    private void getPsr() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getPsr?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchSerializer());
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentSerializer());
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Psr> psrs = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Psr>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Psr, String> psrDao = mMainDbHelper.getPsrDao();
		psrDao.setObjectCache(false); 
		psrDao.delete(psrDao.queryForAll());
		
		for (Psr psr : psrs) {
			psrDao.createOrUpdate(psr);
			
			Log.d("", psr.toString()); 
		}
    }
    
    private void getRoute() throws Exception {
		URL url = new URL(getContext().getString(R.string.syncServer) + "/sm_getRoute?token=" + mAuthToken);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrSerializer()); 
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrDeserializer()); 
		gsonBuilder.registerTypeAdapter(Store.class, new StoreSerializer()); 
		gsonBuilder.registerTypeAdapter(Store.class, new StoreDeserializer());
		gsonBuilder.serializeNulls();
	    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		
		Gson gson = gsonBuilder.create();
		
		List<Route> routes = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Route>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Route, String> routeDao = mMainDbHelper.getRouteDao();
		routeDao.setObjectCache(false); 
		routeDao.delete(routeDao.queryForAll());
		
		for (Route route : routes) {
			routeDao.createOrUpdate(route);
			
			Log.d("", route.toString()); 
		}
    }
    
	private void getStore() throws Exception { 
		URL url = new URL(getContext().getResources().getString(R.string.syncServer) + "/sm_getStore?token=" + mAuthToken);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Customer.class, new CustomerSerializer()); 
		gsonBuilder.registerTypeAdapter(Customer.class, new CustomerDeserializer());
		
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Store> stores = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Store>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Store, String> storeDao = mMainDbHelper.getStoreDao();
		storeDao.setObjectCache(false); 
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);
			
			Log.d("", store.toString()); 
		}
	}
	
	private void getCustomer() throws Exception { 
		URL url = new URL(getContext().getResources().getString(R.string.syncServer) + "/sm_getCustomer?token=" + mAuthToken);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Customer> customers = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Customer>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Customer, String> customerDao = mMainDbHelper.getCustomerDao();
		customerDao.setObjectCache(false); 
		customerDao.delete(customerDao.queryForAll());
		
		for (Customer store : customers) {
			customerDao.createOrUpdate(store);
			
			Log.d("", store.toString()); 
		}
	}
	
	private void getMeasure() throws Exception {   
		URL url = new URL(getContext().getResources().getString(R.string.syncServer) + "/sm_getMeasure?token=" + mAuthToken);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
	    gsonBuilder.setDateFormat("yyyy-MM-dd"); 
		
		Gson gson = gsonBuilder.create();
		
		List<Measure> measures = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Measure>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Measure, String> measureDao = mMainDbHelper.getMeasureDao();
		measureDao.setObjectCache(false); 
		measureDao.delete(measureDao.queryForAll());
		
		for (Measure measure : measures) {
			measureDao.createOrUpdate(measure);
			
			Log.d("", measure.toString()); 
		}
	}

	@Override
	public void onSyncCanceled() {
		super.onSyncCanceled();
		
		sendNotification("canceled"); 
	}

	@Override
	public void onSyncCanceled(Thread thread) {
		super.onSyncCanceled(thread);
		
		sendNotification("canceled"); 
	}

}
