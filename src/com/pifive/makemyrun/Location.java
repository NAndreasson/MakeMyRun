package com.pifive.makemyrun;

public class Location {

	private double lat;
	private double lng;
	
	private int latInt;
	private int lngInt;
	
	public Location(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
		
		latInt = new Double(lat).intValue();
		lngInt = new Double(lng).intValue();
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLng(){
		return lng;
	}
	
	public int getLatInt() {
		return latInt;
	}
	
	public int getLngInt() {
		return lngInt;
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
