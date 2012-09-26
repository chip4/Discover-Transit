package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class ItemizedOverlayActivity extends BalloonItemizedOverlay<MyOverlayItem> {

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	private Context mContext;
	private MyMapView mapView;
	private int routeNum;
	private boolean isRouteDisplayed = false;
	private RoutePathOverlay routeOverlay;
	private List<Integer> colorList;
	private boolean isStop;
	private Route route;

	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView,int routeNum) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.routeNum = routeNum;
		this.mapView = mapView;
		mContext = mapView.getContext();
		this.route = new Route(routeNum, mContext);
		isStop = true;
		colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.GREEN);
		colorList.add(Color.CYAN);
		colorList.add(Color.BLUE);
		colorList.add(Color.RED);
		colorList.add(Color.RED);
		colorList.add(Color.GREEN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.GREEN);
	}

	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView,int routeNum,boolean isStop) {
		this(defaultMarker, mapView,routeNum);
		this.isStop = isStop;
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
		if(isStop) {
			DisplayArrivalTimeTask showArrivalTime = new DisplayArrivalTimeTask(mContext);
			showArrivalTime.execute(getItem(index).getRouteObject().getURL());
		}
	}


	protected boolean onBalloonTap(int index, MyOverlayItem item) {

		if(!isRouteDisplayed) {
			Drawable draw = mapView.getDrawableList().get(routeNum%10);
			mapView.setRouteDisplayed(true);
			mapView.getOverlays().clear();
			mapView.postInvalidate();
			mapView.getOverlays().add(this);
			ItemizedOverlayActivity busOverlayActivity = new ItemizedOverlayActivity(mapView.getResources().getDrawable(R.drawable.bus), mapView,routeNum,false);
			DisplayBusLocationsTask displayBuses = new DisplayBusLocationsTask(busOverlayActivity,mapView);
			displayBuses.execute(route.getURL());
			
			routeOverlay = new RoutePathOverlay(route.getPathCoords(),colorList.get(routeNum%10));
			mapView.getOverlays().add(routeOverlay);

			mapView.getDbHelper().getStopsforRoute(routeNum, draw, mapView, this);
			this.callPopulate();
			isRouteDisplayed = true;
		}
		else {
			isRouteDisplayed = false;
			mapView.setRouteDisplayed(false);
			mapView.getOverlays().clear();
			mapView.postInvalidate();
			mapView.setForceRefresh(true);
		}
		return true;
	}

}


