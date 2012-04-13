 package com.discovertransit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Stop {
	private MyOverlayItem overlay;
	private int route;
	private String title;
	private String snippet;
	private String stopName;
	private String direction;
	
	
	public Stop(GeoPoint point,String title,String snippet,int route, String stopName, String direction) {
		overlay = new MyOverlayItem(point,title,snippet,route,stopName,direction);
		this.title = title;
		this.snippet = snippet;
		this.route = route;
		this.stopName = stopName;
		this.direction = direction;
	}

	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public MyOverlayItem getOverlay() {
		return overlay;
	}

	public void setOverlay(MyOverlayItem overlay) {
		this.overlay = overlay;
	}

	public int getRoute() {
		return route;
	}

	public void setRoute(int route) {
		this.route = route;
	}
}
