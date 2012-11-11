package com.discovertransit;

import java.util.Collection;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class UpdateMapTask extends AsyncTask<Boolean,Void,Collection<MyOverlayItem>> {

	private double minLat,maxLat,minLon,maxLon;
	private ItemizedOverlayActivity itemizedOverlayActivity;
	private MyMapView mapView;
	private List<Drawable> drawableList;
	private DataBaseHelper dbHelper;

	public UpdateMapTask(ItemizedOverlayActivity itemizedOverlayActivity,MyMapView mapView) {
		this.mapView = mapView;
		this.drawableList = mapView.getDrawableList();
		this.dbHelper = mapView.getDbHelper();
		if(itemizedOverlayActivity==null)
			itemizedOverlayActivity = new ItemizedOverlayActivity(drawableList.get(0),mapView);
		this.itemizedOverlayActivity = itemizedOverlayActivity;
	}

	@Override
	protected void onPreExecute() {
		double latSpan = mapView.getLatitudeSpan()/1E6;
		double lonSpan = mapView.getLongitudeSpan()/1E6;
		maxLat = mapView.getProjection().fromPixels(0, 0).getLatitudeE6()/1E6;
		minLon = mapView.getProjection().fromPixels(0, 0).getLongitudeE6()/1E6;
		minLat = (maxLat - 1.3*latSpan);
		maxLon = (minLon + 1.3*lonSpan);
		maxLat+=.3*latSpan;
		minLon-=.3*lonSpan;
	}

	@Override
	protected Collection<MyOverlayItem> doInBackground(Boolean... params) {
		if(params==null || params[0]==null || dbHelper==null)
			return null;
		Collection<MyOverlayItem> collection = dbHelper.getStopsNearby(minLat,minLon,maxLat,maxLon,params[0],drawableList);
		return collection;
	}

	@Override
	protected void onPostExecute(Collection<MyOverlayItem> collection) {
		if(collection!=null && collection.size()>1 && itemizedOverlayActivity!=null) {
			itemizedOverlayActivity.removeAllOverlays();
			itemizedOverlayActivity.addAllOverlays(collection);
			itemizedOverlayActivity.callPopulate();
		}
		mapView.invalidate();
	}

}