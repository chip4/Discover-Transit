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

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
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

		context = mapView.getContext();
		myMapController = mapView.getController();
		myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestprovider = myLocationManager.getBestProvider(criteria, false);
		/*myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				myMapController.animateTo(myLocationOverlay.getMyLocation());
				myMapController.setZoom(17);
			}
		});*/
		mapView.setOnChangeListener(new MapViewChangeListener());
		Location location = myLocationManager.getLastKnownLocation(bestprovider);
		myMapController.animateTo(new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6)));
		myMapController.setZoom(17);
		ViewTreeObserver vto = mapView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						try {
							doEverything();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}
	
	private class MapViewChangeListener implements MyMapView.OnChangeListener
    {
 
        public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
        {
            // Check values
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
            	refreshMap();
            }
            else if (!newCenter.equals(oldCenter))
            {
                // Map Pan Detected
                // TODO: Add special action here
            	/*try {
					doEverything();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
            	refreshMap();
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
            	refreshMap();
            }
        }
    }
	
	public void refreshMap() {
		List<ItemizedOverlayActivity> refreshList = new ArrayList<ItemizedOverlayActivity>();
		mapOverlays = mapView.getOverlays();
		if(!mapOverlays.isEmpty()) {
			mapOverlays.clear();
			mapView.invalidate();
			
		}
	}

	public void doEverything() throws IOException {

		Drawable drawableMinor = this.getResources().getDrawable(R.drawable.m1);
		itemizedOverlayList = new ArrayList<ItemizedOverlayActivity>();
		/*Iterator<Integer> routeNums = Route.ROUTE_NAMES.keySet().iterator();
		int count = 0;
		while(routeNums.hasNext()) {
			int route = routeNums.next();
			if(count>0) break;
			if(route!= 520 && route!= 521) {
				count++;
			}
		}*/
		addToItemizedOverlayList(itemizedOverlayList,drawableMinor,1,"minor");
		for(int i = 0; i<itemizedOverlayList.size();i++) {
			itemizedOverlayList.get(i).callPopulate();
			mapView.getOverlays().add(itemizedOverlayList.get(i));
		}

		mapView.getOverlays().add(drawBuses(1,this.getResources().getDrawable(R.drawable.bus),mapView));
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

	public ArrayList<Stop> processJSONObject(JSONObject json) throws JSONException {
		ArrayList<Stop> list = new ArrayList<Stop>();
		if(json!=null) {
			JSONArray j =(JSONArray)json.get("data");
			JSONObject obj;
			obj = (JSONObject)j.get(0);
			JSONArray time;
			String nextTime;

			for(int i = 0; i<j.length();i++)
			{
				obj = (JSONObject)j.get(i);
				GeoPoint point = new GeoPoint((int)(obj.getDouble("lat")*1E6),(int)(obj.getDouble("lon")*1E6));
				time = obj.getJSONArray("times");
				if(time.length()>0)
					nextTime = time.get(0).toString();
				else
					nextTime = "[unknown]";
				Stop stop = new Stop(point, obj.getString("stop"), obj.getString("direction")+ "--Next bus arrives at: "+ nextTime,(int)obj.getDouble("route"));
				list.add(stop);
			}

		}
		return list;

	}

	public ItemizedOverlayActivity drawBuses(int route, Drawable drawable, MapView mapView) {
		ItemizedOverlayActivity overlay = new ItemizedOverlayActivity(drawable, mapView,route);
		ArrayList<OverlayItem> list = null;
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

	public ArrayList<OverlayItem> processJSONObjectBusLocation(JSONObject json) throws JSONException {
		ArrayList<OverlayItem> list = new ArrayList<OverlayItem>();
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
				OverlayItem overlayitem = new OverlayItem(point, "Route: " + obj.getInt("route"), obj.getString("direction")+ "--Next Major stop: "+ nextStop);
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

	public void addToItemizedOverlayList(List<ItemizedOverlayActivity> itemizedOverlayList,Drawable draw,int routeNum, String detail) throws IOException {
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
}
