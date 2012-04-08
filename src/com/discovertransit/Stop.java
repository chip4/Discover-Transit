 package com.discovertransit;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Stop {
	private OverlayItem overlay;
	private int route;
	private String title;
	private String snippet;
	
	
	public Stop(GeoPoint point,String title,String snippet,int route) {
		overlay = new OverlayItem(point,title,snippet);
		this.title = title;
		this.snippet = snippet;
		this.route = route;
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

	public OverlayItem getOverlay() {
		return overlay;
	}

	public void setOverlay(OverlayItem overlay) {
		this.overlay = overlay;
	}

	public int getRoute() {
		return route;
	}

	public void setRoute(int route) {
		this.route = route;
	}
}
