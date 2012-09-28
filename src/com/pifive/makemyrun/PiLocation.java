package com.pifive.makemyrun;

public class PiLocation {

	private double lat;
	private double lng;
	
	public PiLocation(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat(){
		return lat;
	}
	
	public double getLng(){
		return lng;
	}
}
