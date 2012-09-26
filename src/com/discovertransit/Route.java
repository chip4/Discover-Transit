package com.discovertransit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class Route{
	private int routeNum;
	private Context context;
	private ArrayList<Bus> buses;
	private ArrayList<ArrayList<GeoPoint>> pathCoords;
	private String name;
	public static final Map<Integer, String> ROUTE_NAMES = new HashMap<Integer, String>();
	
	/*public Route(String name,ArrayList<Bus> buses, ArrayList<ArrayList<GeoPoint>> pathCoords) {
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
	}*/
	public Route(int routeNum, Context context){
		this.routeNum = routeNum;
		this.context = context;
		this.pathCoords = findRouteCoords(routeNum,context);
	}
	
	public ArrayList<ArrayList<GeoPoint>> getPathCoords() {
		return pathCoords;
	}
	public int getRouteNum() {
		return routeNum;
	} void setRouteNum(int routeNum) {
		this.routeNum = routeNum;
	}
	
	public String getURL() {
		return "http://discovertransit.herokuapp.com/bus/"+routeNum+".json";
	}
	
	private ArrayList<ArrayList<GeoPoint>> findRouteCoords(int routeNum,Context context){
		ArrayList<ArrayList<GeoPoint>> finalCoords = new ArrayList<ArrayList<GeoPoint>>();
		try {
			AssetManager am = context.getAssets();
			
			InputStream is = am.open("routecoords/"+routeNum+".txt");
			BufferedReader file = new BufferedReader(new InputStreamReader(is));
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
					GeoPoint a =new GeoPoint((int)(Double.parseDouble(first[1])*1E6),(int)(Double.parseDouble(first[0])*1E6));
					GeoPoint b =new GeoPoint((int)(Double.parseDouble(second[1])*1E6),(int)(Double.parseDouble(second[0])*1E6));
					
					ArrayList<GeoPoint> pair = new ArrayList<GeoPoint>();
					pair.add(a);
					pair.add(b);
					
					finalCoords.add(pair);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalCoords;
	}
	
	
	
	
	static {
		ROUTE_NAMES.put(1, "Centennial Oly. Park/Coronet");
		ROUTE_NAMES.put(2, "Ponce de Leon  Ave/Moreland");
		ROUTE_NAMES.put(3, "Martin Luther King Jr.");
		ROUTE_NAMES.put(4, "Thomasville/Moreland Avenue");
		ROUTE_NAMES.put(5, "Piedmont Road/Sandy Springs");
		ROUTE_NAMES.put(6, "Emory");
		ROUTE_NAMES.put(8, "North Druid Hills Rd.");
		ROUTE_NAMES.put(9, "Toney Valley /Peachcrest Rd.");
		ROUTE_NAMES.put(12, "Howell Mill Rd/Cumberland");
		ROUTE_NAMES.put(13, "Fair Street/Mozley Park");
		ROUTE_NAMES.put(15, "South DeKalb/Candler");
		ROUTE_NAMES.put(16, "Noble");
		ROUTE_NAMES.put(19, "Clairmont Road");
		ROUTE_NAMES.put(21, "Memorial Drive");
		ROUTE_NAMES.put(24, "East Lake/Hosea Williams");
		ROUTE_NAMES.put(25, "Peachtree Industrial Blvd");
		ROUTE_NAMES.put(26, "Perry Boulevard/North Avenue");
		ROUTE_NAMES.put(27, "Cheshire Bridge Rd/Ansley");
		ROUTE_NAMES.put(30, "LaVista Rd");
		ROUTE_NAMES.put(32, "Bouldercrest/Georgia Aquarium");
		ROUTE_NAMES.put(33, "Briarcliff Road/Lenox");
		ROUTE_NAMES.put(34, "Gresham/Clifton Springs");
		ROUTE_NAMES.put(36, "North Decatur Rd/Virginia");
		ROUTE_NAMES.put(37, "Defoors Ferry  Rd./Atlantic");
		ROUTE_NAMES.put(39, "Buford Highway");
		ROUTE_NAMES.put(42, "Pryor Road/McDaniel Street");
		ROUTE_NAMES.put(47, "I-85 Access Rd./Briarwood Rd.");
		ROUTE_NAMES.put(49, "McDonough Boulevard");
		ROUTE_NAMES.put(50, "Donald E Hollowell Pkwy.");
		ROUTE_NAMES.put(51, "Joseph E Boone Blvd/Dixie");
		ROUTE_NAMES.put(53, "Skipper Drive/West Lake");
		ROUTE_NAMES.put(55, "Jonesboro Rd/Hutchens Rd");
		ROUTE_NAMES.put(56, "Adamsville/Collier Heights");
		ROUTE_NAMES.put(58, "Atlanta Industrial/Hollywood");
		ROUTE_NAMES.put(60, "Hightower / Moores Mill");
		ROUTE_NAMES.put(66, "Lynhurst Dr/Barge Rd P/R");
		ROUTE_NAMES.put(67, "West End");
		ROUTE_NAMES.put(68, "Donnelly/Beecher");
		ROUTE_NAMES.put(71, "Cascade  Road");
		ROUTE_NAMES.put(73, "Fulton Industrial");
		ROUTE_NAMES.put(74, "Flat Shoals");
		ROUTE_NAMES.put(75, "Tucker");
		ROUTE_NAMES.put(78, "Cleveland Ave");
		ROUTE_NAMES.put(81, "Venetian Drive/Adams Park");
		ROUTE_NAMES.put(82, "Camp Creek  / Welcome All");
		ROUTE_NAMES.put(83, "Campbellton / Greenbriar");
		ROUTE_NAMES.put(84, "East Point/Camp Creek");
		ROUTE_NAMES.put(85, "Roswell / Mansell Rd");
		ROUTE_NAMES.put(86, "Fairington Rd/McAfee Road");
		ROUTE_NAMES.put(87, "Roswell Rd./Morgan Falls");
		ROUTE_NAMES.put(89, "Flat Shoals Road/Scofield");
		ROUTE_NAMES.put(93, "East Pount/Delowe Drive");
		ROUTE_NAMES.put(95, "Metropolitan Pkwy./ Hapeville");
		ROUTE_NAMES.put(99, "Boulevard/Monroe Drive");
		ROUTE_NAMES.put(103, "N. Shallowford Rd./Peeler Rd.");
		ROUTE_NAMES.put(104, "Winters Chapel Road");
		ROUTE_NAMES.put(107, "Glenwood Road");
		ROUTE_NAMES.put(110, "Peachtree St./&quot;The Peach&quot;");
		ROUTE_NAMES.put(111, "Snapfinger Woods Dr.");
		ROUTE_NAMES.put(114, "Columbia Drive");
		ROUTE_NAMES.put(115, "Covington Highway/South");
		ROUTE_NAMES.put(116, "Redan Road / Stonecrest");
		ROUTE_NAMES.put(117, "Rockbridge Rd./Panola Rd.");
		ROUTE_NAMES.put(119, "Kensington/Hairston Rd.");
		ROUTE_NAMES.put(120, "E. Ponce de Leon Ave/Tucker");
		ROUTE_NAMES.put(121, "Stone Mountain/Memorial Drive");
		ROUTE_NAMES.put(123, "N. DeKalb Mall/Belvedere");
		ROUTE_NAMES.put(124, "Pleasantdale Road");
		ROUTE_NAMES.put(125, "Clarkston/Northlake");
		ROUTE_NAMES.put(126, "Chamblee  / NorthLake");
		ROUTE_NAMES.put(132, "Tilly Mill Road");
		ROUTE_NAMES.put(140, "North Point/Mansell P/R");
		ROUTE_NAMES.put(143, "Windward Park / Ride");
		ROUTE_NAMES.put(148, "Medical Ctr./Riveredge Pkwy.");
		ROUTE_NAMES.put(150, "Perimeter Center/Dunwoody");
		ROUTE_NAMES.put(153, "H E Holmes / Browntown");
		ROUTE_NAMES.put(155, "Windsor St./Lakewood  Ave.");
		ROUTE_NAMES.put(162, "Headland Dr. /Alison  Ct.");
		ROUTE_NAMES.put(165, "Fairburn Rd./Barge Rd.");
		ROUTE_NAMES.put(170, "Brownlee Rd./Peyton");
		ROUTE_NAMES.put(172, "Sylvan Road/Virginia Ave.");
		ROUTE_NAMES.put(178, "Empire Blvd./Southside Ind.");
		ROUTE_NAMES.put(180, "Fairburn / Palmetto");
		ROUTE_NAMES.put(181, "Buffington Rd./South Fulton");
		ROUTE_NAMES.put(183, "Barge P/R /Lakewood");
		ROUTE_NAMES.put(185, "Alpharetta/Holcomb Bridge Rd");
		ROUTE_NAMES.put(186, "Rainbow Dr. / South DeKalb");
		ROUTE_NAMES.put(189, "Old National Hwy/Union");
		ROUTE_NAMES.put(193, "Sylvan Hills");
		ROUTE_NAMES.put(520, "Memorial Drive BRT Limited");
		ROUTE_NAMES.put(521, "Memorial Drive BRT Express");
	}

}
