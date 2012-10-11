package com.discovertransit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class ItemizedOverlayActivity extends BalloonItemizedOverlay<MyOverlayItem> {

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	private Context mContext;
	private MyMapView mapView;
	private boolean isRouteDisplayed = false;
	private List<Integer> colorList;

	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.mapView = mapView;
		mContext = mapView.getContext();
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
	
	public void addAllOverlays(Collection<MyOverlayItem> overlayItems) {
		mOverlays.addAll(overlayItems);
	}
	
	public void removeAllOverlays() {
		mOverlays.clear();
		setLastFocusedIndex(-1);
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

	@Override
	protected void onBalloonOpen(int index) {
		MyOverlayItem currentOverlayItem = mOverlays.get(index);
		if(currentOverlayItem!=null && currentOverlayItem.getRouteObject().isStop()) {
			DisplayArrivalTimeTask showArrivalTime = new DisplayArrivalTimeTask(mContext);
			showArrivalTime.execute(getItem(index).getRouteObject().getURL());
		}
	}


	protected boolean onBalloonTap(int index, MyOverlayItem item) {

		if(!isRouteDisplayed) {
			mapView.setRouteDisplayed(true);
			MyOverlayItem currentOverlayItem = mOverlays.get(index);
			int routeNum = currentOverlayItem.getRouteObject().getRoute();
			mapView.setDisplayedRouteNum(routeNum);
			Route route = new Route(routeNum,mContext);
			Drawable draw = mapView.getDrawableList().get(routeNum%10);
			this.removeAllOverlays();
			this.callPopulate();
			mapView.postInvalidate();
			DisplayBusLocationsTask displayBuses = new DisplayBusLocationsTask(this,mapView,mapView.getResources().getDrawable(R.drawable.bus));
			displayBuses.execute(route.getURL());
			
			mapView.addRoutePathOverlay(new RoutePathOverlay(route.getPathCoords(),colorList.get(routeNum%10)));

			Collection<MyOverlayItem> routeStops = mapView.getDbHelper().getStopsforRoute(routeNum, draw);
			if(routeStops==null) return false;
			this.addAllOverlays(routeStops);
			this.callPopulate();
			isRouteDisplayed = true;
			mapView.postInvalidate();
		}
		else {
			isRouteDisplayed = false;
			mapView.setRouteDisplayed(false);
			this.removeAllOverlays();
			this.callPopulate();
			mapView.removeRoutePathOverlay();
			mapView.postInvalidate();
			mapView.setForceRefresh(true);
		}
		return true;
	}

}


