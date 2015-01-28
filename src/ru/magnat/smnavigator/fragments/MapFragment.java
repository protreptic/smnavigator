package ru.magnat.smnavigator.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.javaprotrepticon.android.androidutils.Fonts;
import org.javaprotrepticon.android.androidutils.Text;
import org.javaprotrepticon.android.widgetutils.widget.StaticMapView;

import ru.magnat.smnavigator.R;
import ru.magnat.smnavigator.activities.MainActivity;
import ru.magnat.smnavigator.location.LocationHelper;
import ru.magnat.smnavigator.model.Psr;
import ru.magnat.smnavigator.model.Route;
import ru.magnat.smnavigator.model.Store;
import ru.magnat.smnavigator.model.parcel.StoreParcel;
import ru.magnat.smnavigator.storage.SecuredStorage;
import ru.magnat.smnavigator.sync.SyncStatus;
import ru.magnat.smnavigator.sync.util.SyncObserver;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.stmt.QueryBuilder;

public class MapFragment extends SupportMapFragment implements SyncObserver {
	
	private LocationHelper mLocationHelper;
	private Account mAccount;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final Bundle arguments = getArguments();
		
		mAccount = arguments.getParcelable("account");
		
		getMapAsync(new OnMapReadyCallback() {
			
			@Override
			public void onMapReady(final GoogleMap googleMap) {
				mSearchAdapter = new SearchAdapter();
				
				mLocationHelper = LocationHelper.get(getActivity(), googleMap, mAccount);
				
				googleMap.setMyLocationEnabled(true);
				
				if (arguments.getBoolean("initialGeopoint")) { 
					getMap().setOnMyLocationChangeListener(null); 
					
					double latitude = arguments.getDouble("latitude");
					double longitude = arguments.getDouble("longitude");
					
					mLocationHelper.moveCameraToLocation(latitude, longitude, 10); 
				} else {
					googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
						
						@Override
						public void onMyLocationChange(Location location) {
							if (location != null) {
								googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 7f));
								googleMap.setOnMyLocationChangeListener(null); 
							}
						}
					});
				}
				
				googleMap.setInfoWindowAdapter(new CustomInfoWindow()); 
				googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						Fragment fragment = null;
						
						Bundle arguments = new Bundle();
						arguments.putParcelable("account", mAccount);
						
						if (marker.getTitle().equals("store")) { 
							fragment = new StoreFragment();
							
							Store store = new Store();
							store.setId(Integer.valueOf(marker.getSnippet()));
							
							arguments.putParcelable("store", new StoreParcel(store));
						} else if (marker.getTitle().equals("psr")) {
							fragment = new PsrFragment();
							
							arguments.putInt("psr_id", Integer.valueOf(marker.getSnippet())); 
						} else {
							return;
						}
						
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
					
				});
				
				mLocationHelper.updateOverlays();
			}
		});

	}	
	 
	public class CustomInfoWindow implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) { 
			Typeface typeface1 = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
			Typeface typeface2 = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Light");
			
			CardView cardView = (CardView) LayoutInflater.from(getActivity()).inflate(ru.magnat.smnavigator.R.layout.custom_infowindow, null, false);
			TextView title = (TextView) cardView.findViewById(R.id.title);
			TextView subtitle = (TextView) cardView.findViewById(R.id.subtitle);
			TextView description = (TextView) cardView.findViewById(R.id.description);
			StaticMapView staticmap = (StaticMapView) cardView.findViewById(R.id.staticmap); 
			TextView staticmaptitle = (TextView) cardView.findViewById(R.id.staticmaptitle); 
			
			title.setTypeface(typeface1); 
			subtitle.setTypeface(typeface2);
			description.setTypeface(typeface2);
			staticmaptitle.setTypeface(typeface2); 
			staticmap.setBackground(getResources().getDrawable(R.drawable.map_unavailable)); 
			
			if (marker == null || marker.getTitle() == null) return null;
			
			if (marker.getTitle().equals("store")) { 
				Store store = new Store();
				
				try {
					GetStoreById getStoreById = new GetStoreById();
					getStoreById.execute(Integer.valueOf(marker.getSnippet()));
					
					store = getStoreById.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				title.setText(store.getCustomer().getName());
				description.setText(store.getAddress());
				subtitle.setText(store.getStoreProperty().getGoldenStatus());
				staticmaptitle.setText(store.getChannel());
				
				Psr psr = new Psr();
				
				try {
					GetPsrByStore getPsrByStore = new GetPsrByStore();
					getPsrByStore.execute(store);
					
					psr = getPsrByStore.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				description.setText(description.getText() + "\n\nТочку обслуживает:\n" + psr.getName() + " (" + psr.getProject() + ")");
			} else if (marker.getTitle().equals("psr")) {
				Psr psr = new Psr();
				
				try {
					GetPsrById getPsrById = new GetPsrById();
					getPsrById.execute(Integer.valueOf(marker.getSnippet()));
					
					psr = getPsrById.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				title.setText(psr.getName());
				subtitle.setText(psr.getBranch().getName());
				description.setText(psr.getDepartment().getName());
				staticmaptitle.setText(psr.getProject());
				staticmap.setGeocoordinate(psr.getLatitude(), psr.getLongitude());  
			} else {
				return null;
			}
			
			return cardView;
		}
		
	}
	
	public class GetPsrByStore extends AsyncTask<Store, Void, Psr> {

		@Override
		protected Psr doInBackground(Store... stores) {
			Psr psr = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				QueryBuilder<Route, Integer> queryBuilder = securedStorage.getRouteDao().queryBuilder();
				queryBuilder.where()
					.eq("store", stores[0].getId());
				
				psr = queryBuilder.queryForFirst().getPsr();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return psr;
		}
		
	}
	
	public class GetStoreById extends AsyncTask<Integer, Void, Store> {

		@Override
		protected Store doInBackground(Integer... params) {
			Store store = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				store = securedStorage.getStoreDao().queryForId(params[0]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return store;
		}
		
	}
	
	public class GetPsrById extends AsyncTask<Integer, Void, Psr> {

		@Override
		protected Psr doInBackground(Integer... params) {
			Psr psr = null;
			
	    	SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				psr = securedStorage.getPsrDao().queryForId(params[0]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return psr;
		}
		
	}
	
	private String mQueryText = "%%";
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		LocationHelper.destroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	public void updateMap() {
		if (mLocationHelper != null) 
			mLocationHelper.updateOverlays();
	}
	
	public class LoadData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mShops.clear();
			
			SecuredStorage securedStorage = new SecuredStorage(getActivity(), mAccount);
			
			try {
				List<Store> shops = securedStorage.getStoreDao().queryBuilder().where().like("name", mQueryText).or().like("address", mQueryText).query();
				
				mShops.addAll(shops);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			securedStorage.closeConnection();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSearchAdapter.notifyDataSetChanged();
			
			showSearchDialog();
		}
		
	}
	
	private List<Store> mShops = new ArrayList<Store>();
	private SearchAdapter mSearchAdapter;
	
	private class SearchAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mShops.size();
		}

		@Override
		public Object getItem(int position) {
			return mShops.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mShops.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.account_item, parent, false);
				
				Typeface typeface = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Regular");
				
				holder = new ViewHolder();
				holder.line1 = (TextView) convertView.findViewById(R.id.line1);
				holder.line1.setTypeface(typeface);
				
				holder.line2 = (TextView) convertView.findViewById(R.id.line2);
				holder.line2.setTypeface(typeface);
				
				convertView.setTag(holder); 
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Store store = (Store) getItem(position);
			
			holder.line1.setText(store.getCustomer().getName());
			holder.line2.setText(Text.prepareAddress(store.getAddress())); 
			
			return convertView;
		}
		
	}
	
	private static class ViewHolder {
		TextView line1;
		TextView line2;
	}
	
	private void showSearchDialog() {
		if (mShops.isEmpty()) {
			Toast.makeText(getActivity(), getString(R.string.emptyList), Toast.LENGTH_LONG).show(); return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
        	.setTitle(null)
        	.setSingleChoiceItems(mSearchAdapter, -1, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int position) {
					dialog.dismiss();
					
					mLocationHelper.showShop(mShops.get(position)); 
				}
			})
			.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					
				}
			})
            .show();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_fragment, menu);
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch));
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				mQueryText = "%" + query + "%";
				
				new LoadData().execute();
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionLegend: {
				new AlertDialog.Builder(getActivity(), 0)
					.setTitle(null) 
					.setCancelable(true)
					.setView(LayoutInflater.from(getActivity()).inflate(R.layout.legend, null, false))  
 					.create()
					.show();
			} break;
			
			default: return super.onOptionsItemSelected(item);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		((MainActivity) getActivity()).registerSyncObserver(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		((MainActivity) getActivity()).unregisterSyncObserver(this);
	}
	
	@Override
	public void onStatusChanged(SyncStatus status) {
		switch (status) {
			case STARTED: {
				
			} break;
			case ACK: {
				
			} break;
			case COMPLETED: {
				updateMap();			
			} break;
			case CANCELED: {
				
			} break;
			case ERROR: {
				
			} break;
			default: {
				throw new RuntimeException();
			}
		}
	}
	
}
