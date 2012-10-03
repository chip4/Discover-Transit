package com.discovertransit;

import com.google.android.maps.GeoPoint;

public class Bus implements RouteObjectInterface {
	private int route;
	private String title;
	private String snippet;
	private String direction;
	private GeoPoint point;
	
	public Bus(GeoPoint location, String title, String snippet, int route, String direction) {
		this.route = route;
		this.snippet = snippet;
		this.title = title;
		this.direction = direction;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public String getTitle() {
		return title;
	}

	public String getSnippet() {
		return snippet;
	}

	public String getURL() {
		return null;
	}

	public int getRoute() {
		return route;
	}

	public String getDirection() {
		return direction;
	}

	public boolean isStop() {
		return false;
	}

	public boolean isBus() {
		return true;
	}
	
	
	
	
}
