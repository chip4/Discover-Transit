package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;
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
	private List<Integer> colorList;
	private boolean isStop;

	public ItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView,int routeNum) {
		super(boundCenterBottom(defaultMarker), mapView);
		this.routeNum = routeNum;
		this.mapView = mapView;
		mContext = mapView.getContext();
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
		super(boundCenterBottom(defaultMarker), mapView);
		this.routeNum = routeNum;
		this.mapView = mapView;
		mContext = mapView.getContext();
		this.isStop = isStop;
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
			this.tempIndex = index;
			try {
				Toast.makeText(mContext, "Next Bus Arrives: " + getItem(tempIndex).getTime(),
						Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				Toast.makeText(mContext, "Next Bus Arrives: [Unknown]",
						Toast.LENGTH_LONG).show();
			}
		}
	}


	protected boolean onBalloonTap(int index, MyOverlayItem item) {

		if(!isRouteDisplayed) {
			Drawable draw = mapView.getDrawableList().get(routeNum%10);
			mapView.setRouteDisplayed(true);
			mapView.getOverlays().clear();
			mapView.postInvalidate();
			mapView.getOverlays().add(this);

			mapView.getOverlays().add(MapViewActivity.drawBuses(routeNum,mapView.getResources().getDrawable(R.drawable.bus),mapView));
			Route route = Route.populateRoute(routeNum,mapView);
			routeOverlay = new RoutePathOverlay(route.getPathCoords(),colorList.get(routeNum%10));
			mapView.getOverlays().add(routeOverlay);
			try {
				mapView.getDbHelper().getStopsforRoute(routeNum, draw, mapView, this);
				this.callPopulate();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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


