package com.discovertransit;

import com.google.android.maps.GeoPoint;

public interface RouteObjectInterface {

	public GeoPoint getPoint();
	
	public String getTitle();
	
	public String getSnippet();
	
	public String getURL();
	
	public int getRoute();
	
	public String getDirection();
	
}
