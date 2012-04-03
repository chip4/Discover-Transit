package com.discovertransit;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class ItemizedOverlayActivity extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private MapView mapView;
	private int routeNum;
	private boolean isRouteDisplayed = false;
	private RoutePathOverlay routeOverlay;
	
	public ItemizedOverlayActivity(Drawable defaultMarker, MapView mapView,int routeNum) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.routeNum = routeNum;
		this.mapView = mapView;
		mContext = mapView.getContext();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
	}
	
	public void callPopulate() {
		populate();
	}
	
	protected boolean onBalloonTap(int index, OverlayItem item) {
		Toast.makeText(mContext, "onBalloonTap for Route: " + routeNum,
				Toast.LENGTH_LONG).show();

		if(!isRouteDisplayed) {
			Route route = Route.populateRoute(routeNum,mapView);
			routeOverlay = new RoutePathOverlay(route.getPathCoords());
			mapView.getOverlays().add(routeOverlay);
			isRouteDisplayed = true;
		}
		else {
			isRouteDisplayed = false;
			mapView.getOverlays().remove(routeOverlay);
			
		}
		return true;
	}
	
	
}


