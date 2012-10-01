package com.pifive.makemyrun;

/**
 * A location defined by a latitude and longitue angles
 */
public class Location {

	private double lat;
	private double lng;
	
	/**
	 * Creates a new location object
	 * @param lat Latitude degrees
	 * @param lng Longitude degrees
	 */
	public Location(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	/**
	 * 
	 * @return Latitude value
	 */
	public double getLat(){
		return lat;
	}
	
	/**
	 * 
	 * @return Longitude value
	 */
	public double getLng(){
		return lng;
	}
	
	/**
	 * 
	 * @return Latitude value in micro-degrees
	 */
	public int getMicroLat(){
		return (int) (lat*1E6);
	}
	
	/**
	 * 
	 * @return Longitude value in micro-degrees
	 */
	public int getMicroLng(){
		return (int) (lng*1E6);
	}

	/**
	 * retruns a string formated like: "Location [lat=xx.x, lng=yy.y]"
	 */
	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
