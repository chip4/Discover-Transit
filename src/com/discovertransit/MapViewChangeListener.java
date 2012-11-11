package com.discovertransit;

//import com.discovertransit.MapViewActivity.UpdateMapTask;
import com.google.android.maps.GeoPoint;

public class MapViewChangeListener implements MyMapView.OnChangeListener
{
	private boolean enabled;
	private ItemizedOverlayActivity itemizedOverlay;
	
	public MapViewChangeListener(ItemizedOverlayActivity itemizedOverlay) {
		this.itemizedOverlay = itemizedOverlay;
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void enable() {
		this.enabled = true;
	}

	public void onChange(MyMapView mapView, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
	{
		if(enabled && (!mapView.isRouteDisplayed()||mapView.forceRefresh())) {
			// Check values
			if(mapView.forceRefresh()) mapView.setForceRefresh(false); 	
			if ((!newCenter.equals(oldCenter)) && (newZoom != oldZoom))
			{
				// Map Zoom and Pan Detected
			}
			else if (!newCenter.equals(oldCenter)&&newZoom>14)
			{
				new UpdateMapTask(itemizedOverlay,mapView).execute(newZoom>18);

				System.out.println("Map Pan Detected");


			}
			else if (newZoom != oldZoom)
			{
				// Map Zoom Detected
			}
		}
	}
}
