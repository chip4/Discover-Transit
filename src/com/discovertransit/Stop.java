 package com.discovertransit;

import com.google.android.maps.GeoPoint;

public class Stop implements RouteObjectInterface{
	private int route;
	private String title;
	private String snippet;
	private String stopName;
	private String direction;
	private GeoPoint point;
	private String baseURL;
	
	
	public Stop(GeoPoint point,String title,String snippet,int route, String stopName, String direction,String baseURL) {
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.route = route;
		this.stopName = stopName;
		this.direction = direction;
		this.baseURL = baseURL;
	}
	
	public String getURL() {
		if(stopName==null || direction==null)
			return null;
		System.out.println(baseURL+route+"/"+direction.toLowerCase()+"/"+stopName.replace(" ","").replace(".", ""));
		//return "http://discovertransit.herokuapp.com/times/"+route+"/"+stopName.replace(" ","").replace(".", "")+"/"+direction+".json";
		return baseURL+route+"/"+direction.toLowerCase()+"/"+stopName.replace(" ","").replace(".", "");
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

	public boolean isStop() {
		return true;
	}

	public boolean isBus() {
		return false;
	}
}
