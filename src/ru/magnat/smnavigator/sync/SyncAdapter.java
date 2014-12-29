package ru.magnat.smnavigator.sync;

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
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.auth.Authenticator;
import ru.magnat.smnavigator.data.DbHelperSecured;
import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Customer;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Manager;
import ru.magnat.smnavigator.model.Measure;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.Target;
import ru.magnat.smnavigator.model.json.BranchDeserializer;
import ru.magnat.smnavigator.model.json.CustomerDeserializer;
import ru.magnat.smnavigator.model.json.CustomerSerializer;
import ru.magnat.smnavigator.model.json.DepartmentDeserializer;
import ru.magnat.smnavigator.model.json.PsrDeserializer;
import ru.magnat.smnavigator.model.json.StoreDeserializer;
import ru.magnat.smnavigator.security.KeyStoreManager;
import ru.magnat.smnavigator.security.MyTrustManager;
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

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    
    @SuppressWarnings("unused")
	private ContentResolver mContentResolver;
    
    private DbHelperSecured mMainDbHelper;
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
		
			mMainDbHelper = DbHelperSecured.get(getContext(), account);
			
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new MyTrustManager(KeyStoreManager.getInstance(getContext()).getKeyStore()) } , null);
			
			getManager();
			getBranch();
			getDepartment();
			getPsr();
			getRoute();
			getStore();
			getCustomer();
			//getMeasure();
			getTarget();
			
			DbHelperSecured.close();
			
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			e.printStackTrace();
			sendNotification("error");	
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
    	Intent intentStarted = new Intent(MainActivity.ACTION_SYNC);
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
		
		Dao<Manager, String> managerDao = mMainDbHelper.getManagerDao();
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
		HttpsURLConnection urlConnection = prepareConnection("sm_getDepartment");
		
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
		HttpsURLConnection urlConnection = prepareConnection("sm_getPsr");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Branch.class, new BranchDeserializer()); 
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
		HttpsURLConnection urlConnection = prepareConnection("sm_getRoute");
    	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Psr.class, new PsrDeserializer()); 
		gsonBuilder.registerTypeAdapter(Store.class, new StoreDeserializer());
		gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		gsonBuilder.serializeNulls();
		
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
		HttpsURLConnection urlConnection = prepareConnection("sm_getStore");
		
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
		HttpsURLConnection urlConnection = prepareConnection("sm_getCustomer");
		
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
	
	@SuppressWarnings("unused")
	private void getMeasure() throws Exception {   
		HttpsURLConnection urlConnection = prepareConnection("sm_getMeasure");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd");
		gsonBuilder.serializeNulls();
		
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
	
	private void getTarget() throws Exception {   
		HttpsURLConnection urlConnection = prepareConnection("sm_getTarget");
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Store.class, new StoreDeserializer());
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.create();
		
		List<Target> targets = gson.fromJson(new JsonReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")), new TypeToken<Collection<Target>>() {}.getType());
		
		urlConnection.disconnect();
		urlConnection = null;
		
		Dao<Target, String> targetDao = mMainDbHelper.getTargetDao();
		targetDao.setObjectCache(false); 
		targetDao.delete(targetDao.queryForAll());
		
		for (Target target : targets) {
			targetDao.createOrUpdate(target);
			
			Log.d("", target.toString()); 
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
