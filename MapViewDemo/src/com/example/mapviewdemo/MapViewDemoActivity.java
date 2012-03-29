package com.example.mapviewdemo;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewDemoActivity extends MapActivity {
	
	//Initial items
	LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	ItemizedOverlayDemo itemizedOverlay;
	
	//Used for location
	LocationManager myLocationManager;
	LocationListener myLocationListener;
	TextView myLongitude, myLatitude;
	MapController myMapController;
	
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
        
        myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
        itemizedOverlay = new ItemizedOverlayDemo(drawable,this);
        /*GeoPoint point = new GeoPoint((int)(myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()*1000000),
        		(int)(myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()*1000000));
        CenterLocation(point);
        OverlayItem overlayitem = new OverlayItem(point,"","");
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);*/
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    private void CenterLocation(GeoPoint point) {
    	myMapController.animateTo(point);
    	//myLongitude.setText("Longitude: " + String.valueOf((float)point.getLongitudeE6()/1000000));
    	//myLatitude.setText("Latutude: " + String.valueOf((float)point.getLatitudeE6()/1000000));

        OverlayItem overlayitem = new OverlayItem(point,"","");
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }
    

	
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			GeoPoint myGeoPoint = new GeoPoint ((int)(loc.getLatitude()*1000000),
					(int)(loc.getLongitude()*1000000));
			CenterLocation(myGeoPoint);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
}