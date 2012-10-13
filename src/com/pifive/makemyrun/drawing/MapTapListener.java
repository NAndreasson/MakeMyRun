package com.pifive.makemyrun.drawing;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public interface MapTapListener {
	public void onTap(GeoPoint geoPoint, MapView mapView);
}
