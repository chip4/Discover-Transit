package com.discovertransit;

import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem{
	private RouteObjectInterface routeObject;
	
	public MyOverlayItem(RouteObjectInterface routeObject) {
		super(routeObject.getPoint(),routeObject.getTitle(),routeObject.getSnippet());
		this.routeObject = routeObject;
	}
	
	public RouteObjectInterface getRouteObject() {
		return routeObject;
	}
	
	public void setRouteObject(RouteObjectInterface routeObject) {
		this.routeObject = routeObject;
	}

}
