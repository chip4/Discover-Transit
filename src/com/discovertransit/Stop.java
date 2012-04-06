 package com.discovertransit;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Stop {
	private OverlayItem overlay;
	private int route;
	
	public Stop(GeoPoint point,String title,String snippet,int route) {
		overlay = new OverlayItem(point,title,snippet);
		this.route = route;
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
