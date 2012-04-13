package com.discovertransit;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class ItemizedOverlayActivity extends BalloonItemizedOverlay<MyOverlayItem> {

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	private Context mContext;
	private MyMapView mapView;
	private int routeNum;
	private boolean isRouteDisplayed = false;
	private RoutePathOverlay routeOverlay;
	private int tempIndex;

	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView,int routeNum) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.routeNum = routeNum;
		this.mapView = mapView;
		mContext = mapView.getContext();
	}

	@Override
	protected MyOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(MyOverlayItem overlay) {
		mOverlays.add(overlay);
	}

	public void callPopulate() {
		populate();
	}

	public ArrayList<MyOverlayItem> getmOverlays() {
		return mOverlays;
	}

	public void setmOverlays(ArrayList<MyOverlayItem> mOverlays) {
		this.mOverlays = mOverlays;
	}

	public int getRouteNum() {
		return routeNum;
	}

	public void setRouteNum(int routeNum) {
		this.routeNum = routeNum;
	}

	@Override
	protected void onBalloonOpen(int index) {
		Toast.makeText(mContext, "[Calculating Next Arrival Time]",
				Toast.LENGTH_LONG).show();
		this.tempIndex = index;
		Runnable run = new Runnable() {
		public void run () {
		if(getItem(tempIndex).getStopName()!=null) {
			try {
				Toast.makeText(mContext, "Next Bus Arrives: " + getItem(tempIndex).getTime(),
						Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				Toast.makeText(mContext, "Next Bus Arrives: [Unknown]",
						Toast.LENGTH_LONG).show();
			}
		}
		}};
		mapView.post(run);
	}


	protected boolean onBalloonTap(int index, MyOverlayItem item) {

		if(!isRouteDisplayed) {
			Drawable draw = mapView.getResources().getDrawable(R.drawable.m2);
			mapView.setRouteDisplayed(true);
			try {
				mapView.getDbHelper().getStopsforRoute(routeNum, draw, mapView, this);
				this.callPopulate();
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
			mapView.setRouteDisplayed(false);
			mapView.getOverlays().remove(routeOverlay);
			mapView.getOverlays().remove(this);
			mapView.setForceRefresh(true);

		}
		return true;
	}



}


