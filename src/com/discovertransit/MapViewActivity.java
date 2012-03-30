package com.discovertransit;

import java.util.ArrayList;
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
		//List<GeoPoint> path = new ArrayList<GeoPoint>();
		//mapView.getOverlays().add(new RoutePathOverlay(path));
		
		//List<Overlay> mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new ItemizedOverlayActivity(drawable, mapView);
		
		point = new GeoPoint(33753475,-84392002);
		OverlayItem overlayitem = new OverlayItem(point, "Alabama & Broad St.", "Northbound");
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
   
}