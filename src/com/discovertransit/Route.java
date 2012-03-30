package com.discovertransit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.util.Pair;

public class Route extends Activity{
	private int routeNum;
	private ArrayList<Bus> buses;
	private ArrayList<ArrayList<GeoPoint>> pathCoords;
	private String name;
	
	InputStream is;
	
	public Route(String name,ArrayList<Bus> buses, ArrayList<ArrayList<GeoPoint>> pathCoords) {
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
	public ArrayList<ArrayList<GeoPoint>> getPathCoords() {
		return pathCoords;
	}
	public void setPathCoords(ArrayList<ArrayList<GeoPoint>> coords) {
		this.pathCoords = (ArrayList<ArrayList<GeoPoint>>) coords.clone();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRouteNum() {
		return routeNum;
	} void setRouteNum(int routeNum) {
		this.routeNum = routeNum;
	}
	
	public static Route getRoute(int routeNum){
		Route ret = new Route();
		ret.setRouteNum(routeNum);
		ArrayList<ArrayList<GeoPoint>> finalCoords = new ArrayList<ArrayList<GeoPoint>>();
		try {
			System.out.println("../routecoords/"+routeNum+".txt");
			BufferedReader file = new BufferedReader(new FileReader("../routecoords/"+routeNum+".txt"));
			String line;
			while((line = file.readLine()) != null){
				String[] splitOnSemi = line.split(";");
				for(int i=0; i<splitOnSemi.length;i++){
					int j = i+1;
					if(i == splitOnSemi.length-1){
						j=splitOnSemi.length - 2;
					}
					//need {splitOnSemi[0],splitOnSemi[1]}
					String[] first = splitOnSemi[i].split(" ");
					String[] second = splitOnSemi[j].split(" ");
					
					ArrayList<GeoPoint> pair = new ArrayList<GeoPoint>();
					pair.add(new GeoPoint((int)(Double.parseDouble(first[1])*1E6),(int)(Double.parseDouble(first[0])*1E6)));
					pair.add(new GeoPoint((int)(Double.parseDouble(second[1])*1E6),(int)(Double.parseDouble(second[0])*1E6)));
					
					finalCoords.add(pair);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
