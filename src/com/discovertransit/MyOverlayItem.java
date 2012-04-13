package com.discovertransit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem{
	private GeoPoint point;
	private String title;
	private String snippet;
	private int routeNum;
	private String stopName;
	private String direction;

	public MyOverlayItem(GeoPoint point, String title, String snippet, int routeNum) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.routeNum = routeNum;
		this.stopName = null;
		this.direction = null;
	}

	public MyOverlayItem(GeoPoint point, String title, String snippet, int routeNum, String stopName, String direction) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.routeNum = routeNum;
		this.stopName = stopName;
		this.direction = direction;
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

	public String getSnippet() {
		return snippet;
	}

	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
	}

	public String getStopName() {
		return this.stopName;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() throws JSONException {

		String nextTime = "[unknown]";
		if(stopName!=null && direction!=null) {
			JSONArray time;
			stopName = stopName.replace(" ","");
			stopName = stopName.replace(".", "");
			JSONObject json = MapViewActivity.connect("http://discovertransit.herokuapp.com/times/"+routeNum+"/"+stopName+"/"+direction+".json");
			if(json!=null) {
				try {
					json = (JSONObject) json.get("data");
					time = json.getJSONArray("times");
					if(time.length()>0)
						nextTime = time.get(0).toString();
					else
						nextTime = "[unknown]";
				} catch (JSONException e) {
					nextTime ="[unknown]";
				}
			}
		}
		return nextTime;
	}

}
