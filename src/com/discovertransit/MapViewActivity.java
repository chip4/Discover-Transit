package com.discovertransit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.SearchView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MapViewActivity extends MapActivity implements LocationListener {

	//Initial items
	LinearLayout linearLayout;
	MyMapView mapView;
	ItemizedOverlayActivity itemizedOverlay;
	
	//Used for location
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	MapController myMapController;
	MyLocationOverlay myLocationOverlay;
	Context context;
	List<Drawable> drawableList;

	//Database
	DataBaseHelper dbHelper;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		new ServerWakeup().execute();
		mapView = (MyMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		myLocationOverlay = new MyLocationOverlay(this,mapView);
		myLocationOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocationOverlay);

		dbHelper = new DataBaseHelper(mapView.getContext());
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDataBase();
		} catch(SQLException e) {
			throw e;
		}
		mapView.setDbHelper(dbHelper);
		context = mapView.getContext();
		myMapController = mapView.getController();
		myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestprovider = myLocationManager.getBestProvider(criteria, false);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				myMapController.animateTo(myLocationOverlay.getMyLocation());
				myMapController.setCenter(myLocationOverlay.getMyLocation());
				myMapController.setZoom(17);
			}
		});
		mapView.setOnChangeListener(new MapViewChangeListener());
		Location location = myLocationManager.getLastKnownLocation(bestprovider);
		GeoPoint p;
		try {
			p = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
		} catch(Exception e) {
			p = new GeoPoint(33760204,-84386222);
		}
		myMapController.animateTo(p);
		myMapController.setZoom(17);
		myMapController.setCenter(p);
		
		Runnable waitForMap = new Runnable() {
			public void run() {
				if(mapView.getWidth()==0||mapView.getHeight()== 0) {
					mapView.postDelayed(this, 100);
				}
				else {
					try {
						drawableList = getDrawableList();
						itemizedOverlay = new ItemizedOverlayActivity(drawableList.get(0),mapView);
						mapView.getOverlays().add(itemizedOverlay);
						new UpdateMapTask(itemizedOverlay).execute(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		mapView.postDelayed(waitForMap, 100);
	}

	private class MapViewChangeListener implements MyMapView.OnChangeListener
	{

		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
		{
			if(!mapView.isRouteDisplayed()||mapView.forceRefresh()) {
				// Check values
				if(mapView.forceRefresh()) mapView.setForceRefresh(false); 	
				if ((!newCenter.equals(oldCenter)) && (newZoom != oldZoom))
				{
					// Map Zoom and Pan Detected
				}
				else if (!newCenter.equals(oldCenter)&&newZoom>14)
				{
					new UpdateMapTask(itemizedOverlay).execute(newZoom>18);
					
					System.out.println("Map Pan Detected");
					
					
				}
				else if (newZoom != oldZoom)
				{
					// Map Zoom Detected
				}
			}
		}
	}

	private class UpdateMapTask extends AsyncTask<Boolean,Void,Collection<MyOverlayItem>> {
		
		private double minLat,maxLat,minLon,maxLon;
		private ItemizedOverlayActivity itemizedOverlayActivity;
		
		public UpdateMapTask(ItemizedOverlayActivity itemizedOverlayActivity) {
			if(itemizedOverlayActivity==null)
				itemizedOverlayActivity = new ItemizedOverlayActivity(drawableList.get(0),mapView);
			this.itemizedOverlayActivity = itemizedOverlayActivity;
		}
		
		@Override
		protected void onPreExecute() {
			double latSpan = mapView.getLatitudeSpan()/1E6;
			double lonSpan = mapView.getLongitudeSpan()/1E6;
			maxLat = mapView.getProjection().fromPixels(0, 0).getLatitudeE6()/1E6;
			minLon = mapView.getProjection().fromPixels(0, 0).getLongitudeE6()/1E6;
			minLat = (maxLat - 1.3*latSpan);
			maxLon = (minLon + 1.3*lonSpan);
			maxLat+=.3*latSpan;
			minLon-=.3*lonSpan;
		}
		
		@Override
		protected Collection<MyOverlayItem> doInBackground(Boolean... params) {
			if(params==null || params[0]==null)
				return null;
			Collection<MyOverlayItem> collection = dbHelper.getStopsNearby(minLat,minLon,maxLat,maxLon,params[0],drawableList);
			return collection;
		}
		
		@Override
		protected void onPostExecute(Collection<MyOverlayItem> collection) {
			if(collection!=null && itemizedOverlayActivity!=null) {
				itemizedOverlayActivity.removeAllOverlays();
				itemizedOverlayActivity.addAllOverlays(collection);
				itemizedOverlayActivity.callPopulate();
				
			}
			mapView.invalidate();
		}
		
	}

	private List<Drawable> getDrawableList() {
		if(drawableList==null || drawableList.size()<10) {
			drawableList = new ArrayList<Drawable>();
			drawableList.add(this.getResources().getDrawable(R.drawable.m1));
			drawableList.add(this.getResources().getDrawable(R.drawable.m2));
			drawableList.add(this.getResources().getDrawable(R.drawable.m3));
			drawableList.add(this.getResources().getDrawable(R.drawable.m4));
			drawableList.add(this.getResources().getDrawable(R.drawable.m5));
			drawableList.add(this.getResources().getDrawable(R.drawable.m6));
			drawableList.add(this.getResources().getDrawable(R.drawable.m7));
			drawableList.add(this.getResources().getDrawable(R.drawable.m8));
			drawableList.add(this.getResources().getDrawable(R.drawable.m9));
			drawableList.add(this.getResources().getDrawable(R.drawable.m10));
		}
		return drawableList;
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onPause() {
		myLocationOverlay.disableMyLocation();
		myLocationManager.removeUpdates(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}


	private class ServerWakeup extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet("http://discovertransit.herokuapp.com/"); 

			// Execute the request
			try {
				httpclient.execute(httpget);

			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}


	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);
        
	    return super.onCreateOptionsMenu(menu);
	}
	
}
