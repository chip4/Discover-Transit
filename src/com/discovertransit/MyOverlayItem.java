package com.discovertransit;

import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem{
	private RouteObjectInterface routeObject;
	private Drawable marker;
	public MyOverlayItem(Drawable marker, RouteObjectInterface routeObject) {
		super(routeObject.getPoint(),routeObject.getTitle(),routeObject.getSnippet());
		this.marker = marker;
		this.routeObject = routeObject;
	}
	
	public RouteObjectInterface getRouteObject() {
		return routeObject;
	}
	
	public void setRouteObject(RouteObjectInterface routeObject) {
		this.routeObject = routeObject;
	}
	
	@Override
	public Drawable getMarker(int stateBitSet) {
		marker.setBounds(-marker.getIntrinsicWidth()/2,-marker.getIntrinsicHeight(),marker.getIntrinsicWidth()/2,0);
		return marker;
	}

}
