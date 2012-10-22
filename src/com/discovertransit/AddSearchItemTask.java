package com.discovertransit;

import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import android.os.AsyncTask;

public class AddSearchItemTask extends AsyncTask<Void,Void,SearchOverlayItem> {

	private SearchItemizedOverlay itemizedOverlay;
	private String url;
	private MyMapView mapView;
	public AddSearchItemTask(MyMapView mapView, SearchItemizedOverlay itemizedOverlay, String url) {
		this.itemizedOverlay=itemizedOverlay;
		this.url=url;
		this.mapView=mapView;
	}
	@Override
	protected SearchOverlayItem doInBackground(Void... arg0) {
		SearchOverlayItem overlayItem = null;
		if(itemizedOverlay!=null && url!=null) {
			try {
				StringBuilder jsonResults = new StringBuilder();
				URL urlObject = new URL(url);
				InputStreamReader in = new InputStreamReader(urlObject.openStream());
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
				JSONObject jsonObject = new JSONObject(jsonResults.toString()).getJSONObject("result");
				String name = jsonObject.getString("name");
				JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
				GeoPoint geoPoint = new GeoPoint((int)(location.getDouble("lat")*1E6),(int)(location.getDouble("lng")*1E6));
				overlayItem = new SearchOverlayItem(geoPoint,name,"");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return overlayItem;
	}
	
	protected void onPostExecute(SearchOverlayItem overlayItem) {
		if(overlayItem!=null) {
			itemizedOverlay.removeAllOverlays();
			itemizedOverlay.addOverlay(overlayItem);
			mapView.getController().animateTo(overlayItem.getPoint());
		}
		mapView.invalidate();
	}
	
	

}
