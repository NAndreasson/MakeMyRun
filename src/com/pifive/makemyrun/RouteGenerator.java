/**
 * RouteGenerator.java
 */
package com.pifive.makemyrun;

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
