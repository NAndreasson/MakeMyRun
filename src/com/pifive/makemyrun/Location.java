package com.pifive.makemyrun;

public class Location {

	private double lat;
	private double lng;
	
	public Location(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLng(){
		return lng;
	}
	
	public int getMicroLat(){
		return (int) (lat*1E6);
	}
	
	public int getMicroLng(){
		return (int) (lng*1E6);
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
