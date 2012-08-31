package com.discovertransit;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

public class OverlappingStopItemizedOverlayActivity extends ItemizedOverlay<OverlappingOverlayItem> {

	private List<OverlappingOverlayItem> mOverlays = new ArrayList<OverlappingOverlayItem>();
	
	
	public OverlappingStopItemizedOverlayActivity(Drawable defaultMarker, MyMapView mapView) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlappingOverlayItem createItem(int index) {
		return mOverlays.get(index);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlappingOverlayItem item) {
		mOverlays.add(item);
	}

	public void callPopulate() {
		populate();
	}
}
