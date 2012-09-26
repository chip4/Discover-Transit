 package com.discovertransit;

import com.google.android.maps.GeoPoint;

public class Stop implements RouteObjectInterface{
	private int route;
	private String title;
	private String snippet;
	private String stopName;
	private String direction;
	private GeoPoint point;
	
	
	public Stop(GeoPoint point,String title,String snippet,int route, String stopName, String direction) {
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.route = route;
		this.stopName = stopName;
		this.direction = direction;
	}
	
	public String getURL() {
		if(stopName==null || direction==null)
			return null;
		System.out.println("http://discovertransit.herokuapp.com/times/"+route+"/"+stopName.replace(" ","").replace(".", "")+"/"+direction+".json");
		return "http://discovertransit.herokuapp.com/times/"+route+"/"+stopName.replace(" ","").replace(".", "")+"/"+direction+".json";
	}

	public GeoPoint getPoint() {
		return point;
	}
	
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getRoute() {
		return route;
	}

	public void setRoute(int route) {
		this.route = route;
	}
}
