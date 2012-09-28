/**
 * RouteGenerator.java
 */
package com.pifive.makemyrun;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 *	RouteGenerator
 *	Class with static methods for route generation.
 */
public class RouteGenerator {

	/**
	 * Private constructor to prevent from being instantiated
	 */
	private RouteGenerator() {
		
	}
	
	public static List<Location> generateRoute(final Location startEndLoc) {
		
		// generate centerpoint in the 'circle' 


		return null;

		// calculate distance 
		
		// use sin and cos x to calculate a number of new waypoints
		
		// add the waypoints to lists
		
		// return the list
	}
	
	/**
	 * Generates a location with coordinates 0.003 - 0.007 latitude and 
	 * longitude from the passed location
	 * @param location
	 * @return
	 */
	public static PiLocation generateRandomLocation(PiLocation location) {
		// create another location approx 0.003 - 0.007 from the current
		Random random = new Random();
		double randomNumber = 0.003 + random.nextDouble() * 0.004;
		double centerLatitude = location.getLat() + randomNumber;
		double centerLongitude = location.getLat() + randomNumber;
		
		return new PiLocation(centerLatitude, centerLongitude);
	}
	
	/**
	 * Returns current Location
	 * @param context
	 * @return
	 */
	public static Location getCurrentLocation(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(new Criteria(), false);
		
		return locationManager.getLastKnownLocation(provider);
	}
	
	/**
	 * Method to print current location to console.
	 */
	public static String printableCurrentLocation(Context context) {
		Location location = getCurrentLocation(context);
		return "" + location.getLatitude() + " and " + location.getLongitude();
	}
	
	/**
	 * Returns X number of points in a circle, all with the same distance from center.
	 */
	public static List<PiLocation> getCircle(PiLocation center, PiLocation start, int points) {
		if (points < 2) {
			throw new IllegalArgumentException("At least two points are needed");
		} else if (points > 10) {
			throw new IllegalArgumentException("No more than 10 points");
		}
		
		List<PiLocation> locations = new ArrayList<PiLocation>();
		double angle = (Math.PI * 2) / (double) points;
		double longDiff = start.getLng() - center.getLng();
		double latDiff = start.getLat() - center.getLat();
		double radius = Math.sqrt(Math.pow(longDiff, longDiff) + Math.pow(latDiff, latDiff));
		
		locations.add(start);
		
		for(int i=1; i<points; i++) {
			locations.add(new PiLocation(center.getLat() + Math.cos(angle*i)*radius, 
									   center.getLng() + Math.sin(angle*i)*radius));
		}
		
		return locations;
	}
}
