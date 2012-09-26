package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.os.AsyncTask;

public class DisplayBusLocationsTask extends AsyncTask<String,Void,List<MyOverlayItem>> {

	private ItemizedOverlayActivity busOverlayActivity;
	private MapView mapView;
	public DisplayBusLocationsTask(ItemizedOverlayActivity busOverlayActivity,MapView mapView) {
		this.busOverlayActivity = busOverlayActivity;
		this.mapView = mapView;
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
				MyOverlayItem overlayitem = new MyOverlayItem(new Bus(point, "Route: " + busData.getInt("route"), busData.getString("direction")+ "--Next Major stop: "+ nextStop,busData.getInt("route"),busData.getString("direction")));
				busList.add(overlayitem);

			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(List<MyOverlayItem> busList) {
		if(busList==null || busOverlayActivity==null || mapView==null) {
			return;
		}
		
		for(int i = 0; i<busList.size();i++)
			busOverlayActivity.addOverlay(busList.get(i));

		System.out.println("Size: "+busList.size());
		busOverlayActivity.callPopulate();
		mapView.getOverlays().add(busOverlayActivity);
		mapView.invalidate();
	}

}
