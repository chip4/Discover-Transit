package com.discovertransit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discovertransit.RoutePathOverlay;

public class MapViewActivity extends MapActivity {
	
	//Initial items
	LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable,drawable2,drawableMajor,drawableMinor,drawable5;
	ItemizedOverlayActivity itemizedOverlay,itemizedOverlay2,itemizedOverlay3,itemizedOverlay4;
	List<ItemizedOverlayActivity> itemizedOverlayList;
	int index = 0;
	
	//Used for location
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	TextView myLongitude, myLatitude;
	MapController myMapController;
	GeoPoint point;
	String best;
    MyLocationOverlay myLocationOverlay;
    Context context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        context = mapView.getContext();
        myMapController = mapView.getController();
        
        mapOverlays = mapView.getOverlays();
        myLocationOverlay = new MyLocationOverlay(this,mapView);
        mapView.getOverlays().add(myLocationOverlay);
        
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                myMapController.animateTo(myLocationOverlay.getMyLocation());
                myMapController.setZoom(18);
            }
        });
		drawableMinor = this.getResources().getDrawable(R.drawable.mini);
		drawableMajor = this.getResources().getDrawable(R.drawable.marker);
		itemizedOverlayList = new ArrayList<ItemizedOverlayActivity>();
		addToItemizedOverlayList(itemizedOverlayList,drawableMinor,1,"minor");
		addToItemizedOverlayList(itemizedOverlayList,drawableMajor,1,"major");
		addToItemizedOverlayList(itemizedOverlayList,drawableMinor,27,"minor");
		addToItemizedOverlayList(itemizedOverlayList,drawableMajor,27,"major");

				
		for(int i = 0; i<itemizedOverlayList.size();i++) {
			itemizedOverlayList.get(i).callPopulate();
			mapView.getOverlays().add(itemizedOverlayList.get(i));
		}
		
		mapView.getOverlays().add(drawBuses(1,this.getResources().getDrawable(R.drawable.marker2),mapView));
		
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    @Override
    public void onPause() {
    	myLocationOverlay.disableMyLocation();
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	myLocationOverlay.enableMyLocation();
    	super.onResume();
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
    
    public ArrayList<OverlayItem> processJSONObject(JSONObject json) throws JSONException {
    	ArrayList<OverlayItem> list = new ArrayList<OverlayItem>();
    	if(json!=null) {
    		JSONArray j =(JSONArray)json.get("data");
    		JSONObject obj;
    		System.out.println(j.length());
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
    			OverlayItem overlayitem = new OverlayItem(point, obj.getString("stop"), obj.getString("direction")+ "--Next bus arrives at: "+ nextTime);
    			list.add(overlayitem);
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
			String nextStop;
			
    		for(int i = 0; i<j.length();i++)
    		{
    			obj = (JSONObject)j.get(i);
    			GeoPoint point = new GeoPoint((int)(obj.getDouble("lat")*1E6),(int)(obj.getDouble("lon")*1E6));
    			nextStop = obj.getString("next_stop");
    			OverlayItem overlayitem = new OverlayItem(point, "Route: " + obj.getInt("route"), obj.getString("direction")+ "--Next Major stop: "+ nextStop);
    			list.add(overlayitem);
    		}
    		
    	}
		return list;
    	
    }
    
    public void addToItemizedOverlayList(List<ItemizedOverlayActivity> itemizedOverlayList,Drawable draw,int routeNum, String detail) {
		ArrayList<OverlayItem> list = null;
    	try {
			list = processJSONObject(connect("http://discovertransit.herokuapp.com/stops/"+routeNum+"/"+detail+".json"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(list!=null) {
			itemizedOverlayList.add(new ItemizedOverlayActivity(draw,mapView,routeNum));
			index++;
			for(int i = 0; i<list.size();i++)
				itemizedOverlayList.get(index-1).addOverlay(list.get(i));
		}
    }
   
}
