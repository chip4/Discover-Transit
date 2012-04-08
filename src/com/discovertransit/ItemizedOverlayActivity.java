package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
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
	private MyMapView mapView;
	private int routeNum;
	private boolean isRouteDisplayed = false;
	private RoutePathOverlay routeOverlay;
	
	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView,int routeNum) {
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
	
	public ArrayList<OverlayItem> getmOverlays() {
		return mOverlays;
	}

	public void setmOverlays(ArrayList<OverlayItem> mOverlays) {
		this.mOverlays = mOverlays;
	}

	public int getRouteNum() {
		return routeNum;
	}

	public void setRouteNum(int routeNum) {
		this.routeNum = routeNum;
	}
	
	protected boolean onBalloonTap(int index, OverlayItem item) {
		Toast.makeText(mContext, "onBalloonTap for Route: " + routeNum,
				Toast.LENGTH_LONG).show();

		if(!isRouteDisplayed) {
			List<ItemizedOverlayActivity> itemizedOverlayList = new ArrayList<ItemizedOverlayActivity>();
			Drawable draw = mapView.getResources().getDrawable(R.drawable.m2);
			mapView.setRouteDisplayed(true);
			mapView.postInvalidate();
			try {
				mapView.getDbHelper().getStopsforRoute(routeNum, draw, mapView, itemizedOverlayList);
				MapViewActivity.displayItemizedOverlayList(itemizedOverlayList,mapView);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mapView.getOverlays().add(MapViewActivity.drawBuses(routeNum,mapView.getResources().getDrawable(R.drawable.bus),mapView));
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


