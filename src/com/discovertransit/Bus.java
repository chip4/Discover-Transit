package com.discovertransit;

import com.google.android.maps.GeoPoint;

public class Bus {
	private GeoPoint location;
	private char direction;
	private String nextStop;
	
	public GeoPoint getLocation() {
		return location;
	}
	public void setLocation(GeoPoint location) {
		this.location = location;
	}
	public char getDirection() {
		return direction;
	}
	public void setDirection(char direction) {
		this.direction = direction;
	}
	public String getNextStop() {
		return nextStop;
	}
	public void setNextStop(String nextStop) {
		this.nextStop = nextStop;
	}
	
	
}
