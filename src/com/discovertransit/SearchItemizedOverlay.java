package com.discovertransit;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class SearchItemizedOverlay extends BalloonItemizedOverlay<SearchOverlayItem> {

	private ArrayList<SearchOverlayItem> searchItems;
	public SearchItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView);
		searchItems = new ArrayList<SearchOverlayItem>();
		
	}

	@Override
	protected SearchOverlayItem createItem(int index) {
		return searchItems.get(index);
	}

	@Override
	public int size() {
		return searchItems.size();
	}
	
	public void addOverlay(SearchOverlayItem overlayItem) {
		searchItems.add(overlayItem);
		populate();
	}
	
	public void removeAllOverlays() {
		searchItems.clear();
		setLastFocusedIndex(-1);
	}
	
	public void removeOverlay(int index) {
		searchItems.remove(index);
		setLastFocusedIndex(-1);
		populate();
	}
	
	protected boolean onBalloonTap(int index, SearchOverlayItem currentOverlayItem) {
		removeOverlay(index);
		return false;
		
	}

}
