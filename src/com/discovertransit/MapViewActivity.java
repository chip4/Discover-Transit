package com.discovertransit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
	Drawable drawable;
	ItemizedOverlayActivity itemizedOverlay;
	
	//Used for location
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	TextView myLongitude, myLatitude;
	MapController myMapController;
	GeoPoint point;
	String best;
    MyLocationOverlay myLocationOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        
        myMapController = mapView.getController();
        
        mapOverlays = mapView.getOverlays();
        myLocationOverlay = new MyLocationOverlay(this,mapView);
        mapView.getOverlays().add(myLocationOverlay);
        
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                myMapController.animateTo(myLocationOverlay.getMyLocation());
                myMapController.setZoom(17);
            }
        });
		ArrayList<ArrayList<GeoPoint>> path = new ArrayList<ArrayList<GeoPoint>>();
		path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33789568,-84422128), new GeoPoint(33789569,-84421421)})));
		path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33775040,-84406598), new GeoPoint(33774584,-84406250)})));
		path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33820598,-84449465), new GeoPoint(33821546,-84450228)})));
		path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33752400,-84392820), new GeoPoint(33752730,-84393460)})));
		
		//path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33801811,-84436148), new GeoPoint(33787734,-84412155)})));
		//path.add(new ArrayList(Arrays.asList(new GeoPoint[]{new GeoPoint(33820598,-84449465), new GeoPoint(33821546,-84450228)})));
		
		/*path.add(new GeoPoint(33801811,-84436148));
		path.add(new GeoPoint(33787734,-84412155));
		path.add(new GeoPoint(33770662,-84396068));*/

		mapView.getOverlays().add(new RoutePathOverlay(path));
		
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
   
}