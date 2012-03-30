package com.discovertransit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.util.Pair;

public class Route extends Activity{
	private ArrayList<Bus> buses;
	private ArrayList<Pair<GeoPoint,GeoPoint>> pathCoords;
	private String name;
	
	InputStream is;
	
	public Route(String name,ArrayList<Bus> buses, ArrayList<Pair<GeoPoint,GeoPoint>> pathCoords) {
		this.buses = buses;
		this.pathCoords = pathCoords;
		this.name = name;
	}
	
	public Route(String name,ArrayList<Bus> buses) {
		this.buses = buses;
		this.name = name;
	}
	
	public Route(String name) {
		this.name = name;
	}

	public void generatePathCoords() throws IOException {
		//parse file, create pairs and add to arraylist
		try {
			is = getAssets().open(name+".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			//while((line = reader.))
			
		} catch(Exception E) {
			throw new RuntimeException(E);
		} finally {
			if(is!=null)
				is.close();
		}
	}
	
	public ArrayList<Bus> getBuses() {
		return buses;
	}
	public void addBus(Bus bus) {
		this.buses.add(bus);
	}
	public ArrayList<Pair<GeoPoint, GeoPoint>> getPathCoords() {
		return pathCoords;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
