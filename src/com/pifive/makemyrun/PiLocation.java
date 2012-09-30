package com.pifive.makemyrun;

public class PiLocation {

	private double lat;
	private double lng;
	
	public PiLocation(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public int getMicroLat() {
		return Double.valueOf(lat*1E6).intValue();
	}
	
	public int getMicroLng() {
		return Double.valueOf(lng*1E6).intValue();
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
