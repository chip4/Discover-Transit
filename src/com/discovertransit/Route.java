package com.discovertransit;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.util.Pair;

public class Route {
	private ArrayList<Bus> buses;
	private ArrayList<Pair<GeoPoint,GeoPoint>> pathCoords;
	private String name;
	
	
	public ArrayList<Bus> getBuses() {
		return buses;
	}
	public void addBus(Bus bus) {
		this.buses.add(bus);
	}
	public ArrayList<Pair<GeoPoint, GeoPoint>> getPathCoords() {
		return pathCoords;
	}
	public void setPathCoords(String routeFile) {
		//parse file, create pairs and add to arraylist
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
