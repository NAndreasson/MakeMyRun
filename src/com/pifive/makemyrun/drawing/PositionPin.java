package com.pifive.makemyrun.drawing;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;

public class PositionPin {	
	private GeoPoint location;
	private Bitmap image;
	
	public PositionPin(GeoPoint startLocation, Bitmap image) {
		this.location = startLocation;
		this.image = image;
	}
	
	public GeoPoint getGeoPoint() {
		return location;
	}
	
	public void setGeoPoint(GeoPoint location) {
		this.location = location;
	}
	
	public Bitmap getImage() {
		return image;
	}
}
