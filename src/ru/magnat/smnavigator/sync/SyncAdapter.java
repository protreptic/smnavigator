package ru.magnat.smnavigator.sync;

import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Georegion;
import ru.magnat.smnavigator.model.Location;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.StoreProperty;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.model.json.BranchDeserializer;
import ru.magnat.smnavigator.model.json.CustomerDeserializer;
import ru.magnat.smnavigator.model.json.CustomerSerializer;
import ru.magnat.smnavigator.model.json.DepartmentDeserializer;
import ru.magnat.smnavigator.model.json.LocationDeserializer;
import ru.magnat.smnavigator.model.json.PsrDeserializer;
import ru.magnat.smnavigator.model.json.StoreDeserializer;
import ru.magnat.smnavigator.model.json.StorePropertyDeserializer;
import ru.magnat.smnavigator.model.json.StorePropertySerializer;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.j256.ormlite.dao.Dao;

public class SyncAdapter extends BaseSyncAdapter {
    
	private SecuredStorage mSecuredStorage;
	
    public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	super.onPerformSync(account, extras, authority, provider, syncResult);
    	
    	sendNotification("started");
    	
    	startAck();
    	
		try {
			mSecuredStorage = new SecuredStorage(getContext(), account);
			
			getManager();
			getBranch();
			getLocation();
			getDepartment();
			getPsr();
			getRoute();
			getStore();
			getStoreProperties();
			getMeasure();
			getCustomer();
			getTarget();
			getGeoregion();
			
			mSecuredStorage.closeConnection();
		} catch (Exception e) {
			stopAck();
			sendNotification("error"); 
			e.printStackTrace();
	    }
		
		stopAck();
		
		saveLastSync();
		
		sendNotification("completed");
    }
    
	private void saveLastSync() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(mAccount.name + ".global", Context.MODE_MULTI_PROCESS);
        
        Editor editor = sharedPreferences.edit();
        editor.putString("lastSync", new Timestamp(System.currentTimeMillis()).toString());
        editor.commit();
    }
	
    private void getManager() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getManager");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Manager> managers = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Manager>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Manager, Integer> managerDao = mSecuredStorage.getManagerDao();
		managerDao.setObjectCache(false); 
		managerDao.delete(managerDao.queryForAll());
		
		for (Manager manager : managers) {
			managerDao.createOrUpdate(manager);
		}
    }
    
    private void getBranch() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getBranch");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Location.class, new LocationDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Branch> branches = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Branch>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Branch, Integer> branchDao = mSecuredStorage.getBranchDao();
		branchDao.setObjectCache(false); 
		branchDao.delete(branchDao.queryForAll());
		
		for (Branch branch : branches) {
			branchDao.createOrUpdate(branch);
		}
    }
    
    private void getLocation() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getLocation");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Location> locations = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Location>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Location, Integer> locationDao = mSecuredStorage.getLocationDao();
		locationDao.setObjectCache(false); 
		locationDao.delete(locationDao.queryForAll());
		
		for (Location location : locations) {
			locationDao.createOrUpdate(location);
		}
    }
    
    private void getDepartment() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getDepartment");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Department> departments = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Department>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Department, Integer> departmentDao = mSecuredStorage.getDepartmentDao();
		departmentDao.setObjectCache(false); 
		departmentDao.delete(departmentDao.queryForAll());
		
		for (Department department : departments) {
			departmentDao.createOrUpdate(department);
		}
    }
    
    private void getPsr() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getPsr");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer()); 
		gsonBuilder.registerTypeAdapter(Department.class, new DepartmentDeserializer()); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Psr> psrs = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Psr>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Psr, Integer> psrDao = mSecuredStorage.getPsrDao();
		psrDao.setObjectCache(false); 
		psrDao.delete(psrDao.queryForAll());
		
		for (Psr psr : psrs) {
			psrDao.createOrUpdate(psr);
		}
    }
    
    private void getRoute() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getRoute");
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrDeserializer()); 
		gsonBuilder.registerTypeAdapter(Store.class, new StoreDeserializer());
		gsonBuilder.setDateFormat("yyyy-MM-dd"); 
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Route> routes = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Route>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Route, Integer> routeDao = mSecuredStorage.getRouteDao();
		routeDao.setObjectCache(false); 
		routeDao.delete(routeDao.queryForAll());
		
		for (Route route : routes) {
			routeDao.createOrUpdate(route);
		}
    }
    
	private void getStore() throws Exception { 
		HttpsURLConnection urlConnection = prepareConnection("sm_getStore");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Customer.class, new CustomerSerializer()); 
		gsonBuilder.registerTypeAdapter(Customer.class, new CustomerDeserializer());
		gsonBuilder.registerTypeAdapter(StoreProperty.class, new StorePropertySerializer()); 
		gsonBuilder.registerTypeAdapter(StoreProperty.class, new StorePropertyDeserializer());
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Store> stores = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Store>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Store, Integer> storeDao = mSecuredStorage.getStoreDao();
		storeDao.setObjectCache(false); 
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);
		}
	}
	
	private void getStoreProperties() throws Exception { 
		HttpsURLConnection urlConnection = prepareConnection("sm_getStoreProperty");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<StoreProperty> storeProperties = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<StoreProperty>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<StoreProperty, Integer> storePropertyDao = mSecuredStorage.getStorePropertyDao();
		storePropertyDao.setObjectCache(false); 
		storePropertyDao.delete(storePropertyDao.queryForAll());
		
		for (StoreProperty storeProperty : storeProperties) {
			storePropertyDao.createOrUpdate(storeProperty);
		}
	}
	
	private void getCustomer() throws Exception { 
		HttpsURLConnection urlConnection = prepareConnection("sm_getCustomer");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Customer> customers = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Customer>>() {}.getType());

		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Customer, Integer> customerDao = mSecuredStorage.getCustomerDao();
		customerDao.setObjectCache(false); 
		customerDao.delete(customerDao.queryForAll());
		
		for (Customer store : customers) {
			customerDao.createOrUpdate(store);
		}
	}
	
	private void getMeasure() throws Exception {   
		HttpsURLConnection urlConnection = prepareConnection("sm_getMeasure");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd");
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Measure> measures = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Measure>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Measure, Integer> measureDao = mSecuredStorage.getMeasureDao();
		measureDao.setObjectCache(false); 
		measureDao.delete(measureDao.queryForAll());
		
		for (Measure measure : measures) {
			measureDao.createOrUpdate(measure);
		}
	}
	
	private void getTarget() throws Exception {   
		HttpsURLConnection urlConnection = prepareConnection("sm_getTarget");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Store.class, new StoreDeserializer());
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Target> targets = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Target>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Target, Integer> targetDao = mSecuredStorage.getTargetDao();
		targetDao.setObjectCache(false); 
		targetDao.delete(targetDao.queryForAll());
		
		for (Target target : targets) {
			targetDao.createOrUpdate(target);
		}
	}

    private void getGeoregion() throws Exception {
		HttpsURLConnection urlConnection = prepareConnection("sm_getGeoregion");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Georegion> georegions = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Georegion>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Georegion, Integer> georegionDao = mSecuredStorage.getGeoregionDao();
		georegionDao.setObjectCache(false); 
		georegionDao.delete(georegionDao.queryForAll());
		
		for (Georegion georegion : georegions) {
			georegionDao.createOrUpdate(georegion);
		}
	}
	
	@Override
	public String getActionTag() {
		return SyncManager.ACTION_SYNC;
	}

	@Override
	public String getServerName() {
		return getContext().getString(R.string.mainServerSecure);
	}

}
