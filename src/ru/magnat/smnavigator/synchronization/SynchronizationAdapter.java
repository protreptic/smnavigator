package ru.magnat.smnavigator.synchronization;

import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

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
import ru.magnat.smnavigator.security.KeyStoreManager;
import ru.magnat.smnavigator.security.MyTrustManager;
import ru.magnat.smnavigator.security.account.Authenticator;
import ru.magnat.smnavigator.storage.SecuredStorage;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.j256.ormlite.dao.Dao;

public class SynchronizationAdapter extends AbstractThreadedSyncAdapter {
    
    @SuppressWarnings("unused")
	private ContentResolver mContentResolver;
    
    private SecuredStorage mMainDbHelper;
    private Account mAccount;
    
    private String sessionToken;
    private SSLContext sslContext;
    private HostnameVerifier hostnameVerifier = new HostnameVerifier () {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
		
	};
    
	private HttpsURLConnection prepareConnection(String serviceName) throws Exception {
		URL url = new URL(getContext().getResources().getString(R.string.syncServerSecure) + serviceName + "?token=" + sessionToken);
		 
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection(); 
		httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
		httpsURLConnection.setHostnameVerifier(hostnameVerifier); 
		httpsURLConnection.addRequestProperty("token", sessionToken); 
		
		return httpsURLConnection;
	}
	
    public SynchronizationAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }
    
    public SynchronizationAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        
        mContentResolver = context.getContentResolver();
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;    	
    	
    	sendNotification("started");
    	
    	Authenticator.validateSession(getContext(), account);
		
    	sessionToken = AccountManager.get(getContext()).peekAuthToken(account, account.type);
    	
    	Timer timer = new Timer("ackSender");
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ack");
			}
		}, 0, 1500);
    	
		try {
			TimeUnit.SECONDS.sleep(2);
		
			mMainDbHelper = SecuredStorage.get(getContext(), account);
			
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new MyTrustManager(KeyStoreManager.getInstance(getContext()).getKeyStore()) } , null);
			
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
			
			SecuredStorage.close();
			
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			SecuredStorage.close();
			timer.cancel();
			sendNotification("error"); 
			e.printStackTrace();
			return;
	    }
		
		timer.cancel();
		
		saveLastSync();
		
		sendNotification("completed");
    }
    
	private void saveLastSync() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(mAccount.name + ".global", Context.MODE_MULTI_PROCESS);
        
        Editor editor = sharedPreferences.edit();
        editor.putString("lastSync", new Timestamp(System.currentTimeMillis()).toString());
        editor.commit();
    }
    
    private void sendNotification(String action) {
    	Intent intentStarted = new Intent(SynchronizationManager.ACTION_SYNC);
    	intentStarted.putExtra("action", action);
    	intentStarted.putExtra("account", mAccount.name);
    	
    	getContext().sendBroadcast(intentStarted);
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
		
		Dao<Manager, Integer> managerDao = mMainDbHelper.getManagerDao();
		managerDao.setObjectCache(false); 
		managerDao.delete(managerDao.queryForAll());
		
		for (Manager manager : managers) {
			managerDao.createOrUpdate(manager);
			
			Log.d("", manager.toString()); 
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
		
		Dao<Branch, Integer> branchDao = mMainDbHelper.getBranchDao();
		branchDao.setObjectCache(false); 
		branchDao.delete(branchDao.queryForAll());
		
		for (Branch branch : branches) {
			branchDao.createOrUpdate(branch);
			
			Log.d("", branch.toString()); 
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
		
		Dao<Location, Integer> locationDao = mMainDbHelper.getLocationDao();
		locationDao.setObjectCache(false); 
		locationDao.delete(locationDao.queryForAll());
		
		for (Location location : locations) {
			locationDao.createOrUpdate(location);
			
			Log.d("", location.toString()); 
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
		
		Dao<Department, Integer> departmentDao = mMainDbHelper.getDepartmentDao();
		departmentDao.setObjectCache(false); 
		departmentDao.delete(departmentDao.queryForAll());
		
		for (Department department : departments) {
			departmentDao.createOrUpdate(department);
			
			Log.d("", department.toString()); 
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
		
		Dao<Psr, Integer> psrDao = mMainDbHelper.getPsrDao();
		psrDao.setObjectCache(false); 
		psrDao.delete(psrDao.queryForAll());
		
		for (Psr psr : psrs) {
			psrDao.createOrUpdate(psr);
			
			Log.d("", psr.toString()); 
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
		
		Dao<Route, Integer> routeDao = mMainDbHelper.getRouteDao();
		routeDao.setObjectCache(false); 
		routeDao.delete(routeDao.queryForAll());
		
		for (Route route : routes) {
			routeDao.createOrUpdate(route);
			
			Log.d("", route.toString()); 
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
		
		Dao<Store, Integer> storeDao = mMainDbHelper.getStoreDao();
		storeDao.setObjectCache(false); 
		storeDao.delete(storeDao.queryForAll());
		
		for (Store store : stores) {
			storeDao.createOrUpdate(store);
			
			Log.d("", store.toString()); 
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
		
		Dao<StoreProperty, Integer> storePropertyDao = mMainDbHelper.getStorePropertyDao();
		storePropertyDao.setObjectCache(false); 
		storePropertyDao.delete(storePropertyDao.queryForAll());
		
		for (StoreProperty storeProperty : storeProperties) {
			storePropertyDao.createOrUpdate(storeProperty);
			
			Log.d("", storeProperty.toString()); 
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
		
		Dao<Customer, Integer> customerDao = mMainDbHelper.getCustomerDao();
		customerDao.setObjectCache(false); 
		customerDao.delete(customerDao.queryForAll());
		
		for (Customer store : customers) {
			customerDao.createOrUpdate(store);
			
			Log.d("", store.toString()); 
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
		
		Dao<Measure, Integer> measureDao = mMainDbHelper.getMeasureDao();
		measureDao.setObjectCache(false); 
		measureDao.delete(measureDao.queryForAll());
		
		for (Measure measure : measures) {
			measureDao.createOrUpdate(measure);
			
			Log.d("", measure.toString()); 
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
		
		Dao<Target, Integer> targetDao = mMainDbHelper.getTargetDao();
		targetDao.setObjectCache(false); 
		targetDao.delete(targetDao.queryForAll());
		
		for (Target target : targets) {
			targetDao.createOrUpdate(target);
			
			Log.d("", target.toString()); 
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
		
		Dao<Georegion, Integer> georegionDao = mMainDbHelper.getGeoregionDao();
		georegionDao.setObjectCache(false); 
		georegionDao.delete(georegionDao.queryForAll());
		
		for (Georegion georegion : georegions) {
			georegionDao.createOrUpdate(georegion);
			
			Log.d("", georegion.toString()); 
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