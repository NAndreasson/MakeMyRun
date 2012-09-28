/**
 * RouteGenerator.java
 */
package com.pifive.makemyrun;

import java.util.List;
import java.util.Random;

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
		
		// generate centerpoint in the 'cicle' 
		// create another location approx 0.003 - 0.007 from the current
		Random random = new Random();
		double randomNumber = 0.003 + random.nextDouble() * 0.004;
		double centerLatitude = startEndLoc.getLatitude() + randomNumber;
		double centerLongitude = startEndLoc.getLatitude() + randomNumber;

		return null;

		// calculate distance 
		
		// use sin and cos x to calculate a number of new waypoints
		
		// add the waypoints to lists
		
		// return the list
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
	 * Class to print current location to console.
	 */
	public static String printableCurrentLocation(Context context) {
		Location location = getCurrentLocation(context);
		return "" + location.getLatitude() + " and " + location.getLongitude();
	}
}
