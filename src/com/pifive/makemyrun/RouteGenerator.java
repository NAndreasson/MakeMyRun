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
	
	public static String generateRoute(final PiLocation startEndLoc) {		
		// build the beginning of the google query
		StringBuilder stringBuilder = new StringBuilder("origin=");
		stringBuilder.append(startEndLoc.getLat());
		stringBuilder.append(",");
		stringBuilder.append(startEndLoc.getLng());
		stringBuilder.append("&destination=");
		stringBuilder.append(startEndLoc.getLat());
		stringBuilder.append(",");
		stringBuilder.append(startEndLoc.getLng());
		stringBuilder.append("&waypoints=");
		
		// get the centerpoint of the 'circle'
		PiLocation centerLocation = generateRandomLocation(startEndLoc);
		List<PiLocation> waypoints = getCircle(centerLocation, startEndLoc, 6);
		for (PiLocation waypoint : waypoints) {
			stringBuilder.append(waypoint.getLat());
			stringBuilder.append(",");
			stringBuilder.append(waypoint.getLng());
			stringBuilder.append("|");
		}
		// remove the last |
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("&avoid=highways&sensor=true&mode=walking");
		
		return stringBuilder.toString();
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
		double centerLongitude = location.getLng() + randomNumber;
		
		return new PiLocation(centerLatitude, centerLongitude);
	}
	
	/**
	 * Returns current Location
	 * @param context
	 * @return
	 * @throws NoLocationException 
	 */
	public static Location getCurrentLocation(Context context) throws NoLocationException {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(new Criteria(), false);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location == null) {
			throw new NoLocationException("Location unavailable");
		}
		return location;
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
		double radius = Math.sqrt(Math.pow(longDiff, 2) + Math.pow(latDiff, 2));
		
		for(int i=1; i<points; i++) {
			locations.add(new PiLocation(center.getLat() + Math.cos(angle*i)*radius, 
									   center.getLng() + Math.sin(angle*i)*radius));
		}
		
		return locations;
	}
}
