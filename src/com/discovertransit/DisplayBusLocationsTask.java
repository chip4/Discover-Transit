package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class DisplayBusLocationsTask extends AsyncTask<String,Void,List<MyOverlayItem>> {

	private ItemizedOverlayActivity itemizedOverlayActivity;
	private MapView mapView;
	private Drawable drawable;
	
	public DisplayBusLocationsTask(ItemizedOverlayActivity itemizedOverlayActivity,MapView mapView, Drawable drawable) {
		this.itemizedOverlayActivity = itemizedOverlayActivity;
		this.mapView = mapView;
		this.drawable = drawable;
	}
	@Override
	protected List<MyOverlayItem> doInBackground(String... input) {
		if(input==null || input[0]==null)
			return null;
		String busURL = input[0];
		System.out.println(busURL);
		List<MyOverlayItem> busList = new ArrayList<MyOverlayItem>();
		try {
			JSONArray busDataArray = APIHelper.getJSONArray(busURL);
			JSONObject busData;
			String nextStop = "[unknown]";
			System.out.println(busDataArray.toString());
			for(int i = 0; i<busDataArray.length();i++)
			{
				busData = APIHelper.getJSONArray(busURL).getJSONObject(i);
				GeoPoint point = new GeoPoint((int)(busData.getDouble("lat")*1E6),(int)(busData.getDouble("lon")*1E6));
				if(busData.has("next_stop"))
					nextStop = busData.getString("next_stop");
				MyOverlayItem overlayitem = new MyOverlayItem(drawable,new Bus(point, "Route: " + busData.getInt("route"), busData.getString("direction")+ "--Next Major stop: "+ nextStop,busData.getInt("route"),busData.getString("direction")));
				busList.add(overlayitem);

			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(List<MyOverlayItem> busList) {
		if(busList==null || itemizedOverlayActivity==null || mapView==null) {
			return;
		}
		
		itemizedOverlayActivity.addAllOverlays(busList);
		System.out.println("Size: "+busList.size());
		itemizedOverlayActivity.callPopulate();
		mapView.getOverlays().add(itemizedOverlayActivity);
		mapView.invalidate();
	}

}
