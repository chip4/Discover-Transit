package com.discovertransit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.discovertransit.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapViewActivity extends MapActivity implements LocationListener {

	//Initial items
	LinearLayout linearLayout;
	MyMapView mapView;
	List<Overlay> mapOverlays;
	ItemizedOverlayActivity itemizedOverlay,itemizedOverlay2,itemizedOverlay3,itemizedOverlay4;
	List<ItemizedOverlayActivity> itemizedOverlayList;
	int index = 0;

	//Used for location
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	TextView myLongitude, myLatitude;
	MapController myMapController;
	GeoPoint point;
	String bestprovider;
	MyLocationOverlay myLocationOverlay;
	Context context;
	List<Drawable> drawableList;

	//Database
	DataBaseHelper dbHelper;
	double minLon;
	double minLat;
	double maxLon;
	double maxLat;
	public int count=0;
	public boolean doneUpdating = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

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
		bestprovider = myLocationManager.getBestProvider(criteria, false);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				myMapController.animateTo(myLocationOverlay.getMyLocation());
				myMapController.setCenter(myLocationOverlay.getMyLocation());
				myMapController.setZoom(17);
			}
		});
		mapView.setOnChangeListener(new MapViewChangeListener());
		Location location = myLocationManager.getLastKnownLocation(bestprovider);
		GeoPoint p = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
		myMapController.animateTo(p);
		myMapController.setZoom(17);
		myMapController.setCenter(p);
		/*ViewTreeObserver vto = mapView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						System.out.println(mapView.getMapCenter().toString());
						System.out.println(p.toString());
						try {
							doEverything(); 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//mapView.invalidate();
					}
				});*/
		Runnable waitForMap = new Runnable() {
			public void run() {
				if(mapView.getWidth()==0||mapView.getHeight()== 0) {
					mapView.postDelayed(this, 100);
				}
				else {
					try {
						doEverything();
					} catch (IOException e) {
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
			if(!mapView.isRouteDisplayed()&&(count++>2)||mapView.forceRefresh()) {
				// Check values
				if(mapView.forceRefresh()) mapView.setForceRefresh(false); 	
				if ((!newCenter.equals(oldCenter)) && (newZoom != oldZoom))
				{
					// Map Zoom and Pan Detected
					// TODO: Add special action here
					/*try {
					doEverything();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
					//refreshMap();
				}
				else if (!newCenter.equals(oldCenter)&&newZoom>14)
				{
					// Map Pan Detected

					Runnable updateMap = new Runnable() {
						public void run() {
							itemizedOverlayList = new ArrayList<ItemizedOverlayActivity>();
							minLat = mapView.getProjection().fromPixels(0, 0).getLatitudeE6()/1E6;
							maxLon = mapView.getProjection().fromPixels(0, 0).getLongitudeE6()/1E6;
							maxLat = (minLat - mapView.getLatitudeSpan()/1E6);
							minLon = (maxLon + mapView.getLongitudeSpan()/1E6);
							try {
								dbHelper.getStopsNearby(minLat,minLon,maxLat,maxLon,drawableList,mapView, itemizedOverlayList);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for(int i = 0; i<itemizedOverlayList.size();i++) {
								itemizedOverlayList.get(i).callPopulate();
								mapView.getOverlays().add(itemizedOverlayList.get(i));
							}
							mapView.invalidate();
						}

					};
					runOnUiThread(updateMap);
					System.out.println("Map Pan Detected");
					count=0;
				}
				else if (newZoom != oldZoom)
				{
					// Map Zoom Detected
					// TODO: Add special action here
					/*try {
					doEverything();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
					//refreshMap();
				}
			}
		}
	}

	

	public void doEverything() throws IOException {
		itemizedOverlayList = new ArrayList<ItemizedOverlayActivity>();
		double minLat = mapView.getProjection().fromPixels(0, 0).getLatitudeE6()/1E6;
		double maxLon = mapView.getProjection().fromPixels(0, 0).getLongitudeE6()/1E6;
		double maxLat = (minLat - mapView.getLatitudeSpan()/1E6);
		double minLon = (maxLon + mapView.getLongitudeSpan()/1E6);
		drawableList = new ArrayList<Drawable>();
		drawableList.add(this.getResources().getDrawable(R.drawable.m1));
		drawableList.add(this.getResources().getDrawable(R.drawable.m2));
		drawableList.add(this.getResources().getDrawable(R.drawable.m3));
		drawableList.add(this.getResources().getDrawable(R.drawable.m4));
		drawableList.add(this.getResources().getDrawable(R.drawable.m5));
		drawableList.add(this.getResources().getDrawable(R.drawable.m6));
		drawableList.add(this.getResources().getDrawable(R.drawable.m7));
		int size = 0;
		try {
			size = dbHelper.getStopsNearby(minLat,minLon,maxLat,maxLon,drawableList,mapView,itemizedOverlayList);
			//itemizedOverlayList = dbHelper.getStopsNearby(33.797000000000004,-84.382004,33.784086,-84.392302,drawableList,mapView);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i<size;i++) {
			itemizedOverlayList.get(i).callPopulate();
			mapView.getOverlays().add(itemizedOverlayList.get(i));
		}
		mapView.forceRefresh();
		//mapView.getOverlays().add(drawBuses(1,this.getResources().getDrawable(R.drawable.bus),mapView));
	}

	private void debug(List<ItemizedOverlayActivity> list) {
		for(int i = 0; i<list.size();i++) {
			List<MyOverlayItem> mOverlays = list.get(i).getmOverlays();
			System.out.println("mOverlays size: " + mOverlays.size());
			for(int j = 0; j<mOverlays.size();j++)
				System.out.println(mOverlays.get(j).getTitle());
		}
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

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}


	public static JSONObject connect(String url)
	{

		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpGet httpget = new HttpGet(url); 

		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				String result= convertStreamToString(instream);
				// A Simple JSONObject Creation
				JSONObject json=new JSONObject(result);

				// Closing the input stream will trigger connection release
				instream.close();
				return json;
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static ItemizedOverlayActivity drawBuses(int route, Drawable drawable, MyMapView mapView) {
		ItemizedOverlayActivity overlay = new ItemizedOverlayActivity(drawable, mapView,route);
		ArrayList<MyOverlayItem> list = null;
		try {
			list = processJSONObjectBusLocation(connect("http://discovertransit.herokuapp.com/bus/"+route+".json"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(list!=null) {
			for(int i = 0; i<list.size();i++)
				if(isPointVisible(list.get(i).getPoint(),mapView))
					overlay.addOverlay(list.get(i));
		}
		overlay.callPopulate();

		return overlay;
	}

	public static ArrayList<MyOverlayItem> processJSONObjectBusLocation(JSONObject json) throws JSONException {
		ArrayList<MyOverlayItem> list = new ArrayList<MyOverlayItem>();
		if(json!=null) {
			JSONArray j =(JSONArray)json.get("data");
			JSONObject obj;
			obj = (JSONObject)j.get(0);
			String nextStop = "[unknown]";

			for(int i = 0; i<j.length();i++)
			{
				obj = (JSONObject)j.get(i);
				GeoPoint point = new GeoPoint((int)(obj.getDouble("lat")*1E6),(int)(obj.getDouble("lon")*1E6));
				if(obj.has("next_stop"))
					nextStop = obj.getString("next_stop");
				MyOverlayItem overlayitem = new MyOverlayItem(point, "Route: " + obj.getInt("route"), obj.getString("direction")+ "--Next Major stop: "+ nextStop);
				list.add(overlayitem);

			}

		}
		return list;

	}

	private static boolean isPointVisible(GeoPoint point,MapView mapView) {
		if(point==null) return false;
		Rect currentMapBoundsRect = new Rect();
		Point startPosition = new Point();

		mapView.getProjection().toPixels(point, startPosition);
		mapView.getDrawingRect(currentMapBoundsRect);

		return currentMapBoundsRect.contains(startPosition.x,startPosition.y);
	}

	/*public void addToItemizedOverlayList(List<ItemizedOverlayActivity> itemizedOverlayList,Drawable draw,int routeNum, String detail) throws IOException {
		ArrayList<Stop> list = null;

		try {
			AssetManager am = mapView.getContext().getAssets();
			InputStream is = am.open("stops.json");
			byte[] buffer = new byte[is.available()];
			while((is.read(buffer)) != -1);
			String jsontext = new String(buffer);
			list = processJSONObject(new JSONObject(jsontext));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(list!=null&&!list.isEmpty()) {
			index++;
			int curRoute = list.get(0).getRoute();
			ItemizedOverlayActivity curItemizedOverlay = new ItemizedOverlayActivity(draw,mapView,curRoute);
			for(int i = 0; i<list.size();i++) {
				if(list.get(i).getRoute()!=curRoute) {
					curRoute = list.get(i).getRoute();
					itemizedOverlayList.add(curItemizedOverlay);
					draw = this.getResources().getDrawable(R.drawable.m2);
					curItemizedOverlay = new ItemizedOverlayActivity(draw,mapView,curRoute);
				}
				curItemizedOverlay.addOverlay(list.get(i).getOverlay());
			}
			itemizedOverlayList.add(curItemizedOverlay);
		}
	}*/

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
}
