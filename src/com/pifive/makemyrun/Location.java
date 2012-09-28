package com.pifive.makemyrun;

public class Location {
	private int lat;
	private int lng;
	
	public Location(int lat, int lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLng(){
		return lng;
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
