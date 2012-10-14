/*     Copyright (c) 2012 Johannes Wikner, Anton Lindgren, Victor Lindhe,
 *         Niklas Andreasson, John Hult
 *
 *     Licensed to the Apache Software Foundation (ASF) under one
 *     or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 */

/**
 * RouteGenerator.java
 */
package com.pifive.makemyrun;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.maps.GeoPoint;
import android.location.Location;
import android.util.Log;

/**
 *	RouteGenerator
 *	Class with static methods for route generation.
 */
public abstract class RouteGenerator {

	/**
	 * Private constructor to prevent from being instantiated
	 */
	private RouteGenerator() {
		
	}
	
	/**
	 * Generates a route.
	 * If distance between points < 100 meters, a circle route will be returned.
	 * @param startLoc
	 * @param endLoc
	 * @return
	 */
	public static String generateRoute(final GeoPoint startLoc, final GeoPoint endLoc) {
		Location sLoc = new Location("start location");
		sLoc.setLatitude(startLoc.getLatitudeE6() / 1E6);
		sLoc.setLongitude(startLoc.getLongitudeE6() / 1E6);
		
		Location eLoc = new Location("end location");
		eLoc.setLatitude(endLoc.getLatitudeE6() / 1E6);
		eLoc.setLongitude(endLoc.getLongitudeE6() / 1E6);
		
		System.out.println("distance " + sLoc.distanceTo(eLoc));
		
		if(sLoc.distanceTo(eLoc) < 100.0) {
			return generateCircle(new com.pifive.makemyrun.geo.Location
					(sLoc.getLatitude(), sLoc.getLongitude()));
			
		} else {
			return generateLinear(new com.pifive.makemyrun.geo.Location
					(sLoc.getLatitude(), sLoc.getLongitude()),
					new com.pifive.makemyrun.geo.Location
					(eLoc.getLatitude(), eLoc.getLongitude()));
		}
	}
	
	/**
	 * Returns a string containing a google query with generated waypoints 
	 * @param startEndLoc - Current location that the generated route will start and end at
	 * @return a google query with which you can query google for more steps
	 */
	private static String generateCircle(final com.pifive.makemyrun.geo.Location loc) {		
		// build the beginning of the google query
		StringBuilder stringBuilder = new StringBuilder(startOfQuery(loc, loc));
		
		// get the centerpoint of the 'circle'
		com.pifive.makemyrun.geo.Location centerLocation = generateRandomLocation(loc);
		stringBuilder.append(restOfQuery(getCircle(centerLocation, loc)));
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}
	
	/**
	 * Returns a string containing a google query with generated waypoints
	 * @param aLoc Starting location
	 * @param bLoc Finish location
	 * @return a google query with which you can query google for more steps
	 */
	private static String generateLinear(final com.pifive.makemyrun.geo.Location aLoc,
										 final com.pifive.makemyrun.geo.Location bLoc) {
		
		double longDiff = (bLoc.getLng() - aLoc.getLng());
		double latDiff = (bLoc.getLat() - bLoc.getLat());
		
		List<com.pifive.makemyrun.geo.Location> locations = 
							new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		if (longDiff == 0.0) {
			locations = generateStraightVertical(aLoc, bLoc);
			
		} else if (latDiff == 0.0) {
			locations = generateStraightHorizontal(aLoc, bLoc);
			
		} else {
			double northLat = aLoc.getLat() > bLoc.getLat() ? aLoc.getLat() : aLoc.getLat();
			double southLat = aLoc.getLat() > bLoc.getLat() ? bLoc.getLat() : aLoc.getLat();
			double westLong = aLoc.getLng() < bLoc.getLng() ? aLoc.getLng() : bLoc.getLng();
			double eastLong = aLoc.getLng() < bLoc.getLng() ? bLoc.getLng() : aLoc.getLng();
			
			if ((northLat-southLat)/(eastLong-westLong) < 0.2) {
				locations = generateHorizontalThin(northLat, southLat, westLong, eastLong);
				
			} else if ((eastLong-westLong)/(northLat-southLat) < 0.2) {
				locations = generateVerticalThin(northLat, southLat, westLong, eastLong);
				
			} else {
				locations = generateNormal(northLat, southLat, westLong, eastLong);
				
			}
			
		}
		
		StringBuilder stringBuilder = new StringBuilder(startOfQuery(aLoc, bLoc));
		stringBuilder.append(restOfQuery(locations));
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
		
	}
	
	/**
	 * Generates the start of the google query
	 * @param aLoc
	 * @param bLoc
	 * @return
	 */
	private static String startOfQuery(final com.pifive.makemyrun.geo.Location aLoc,
			 						   final com.pifive.makemyrun.geo.Location bLoc) {
			// build the beginning of the google query
			StringBuilder stringBuilder = new StringBuilder("origin=");
			stringBuilder.append(aLoc.getLat());
			stringBuilder.append(",");
			stringBuilder.append(aLoc.getLng());
			stringBuilder.append("&destination=");
			stringBuilder.append(bLoc.getLat());
			stringBuilder.append(",");
			stringBuilder.append(bLoc.getLng());
			stringBuilder.append("&waypoints=optimize:true|");
			
			return stringBuilder.toString();
	}
	
	/**
	 * Generates the rest of the query
	 * @param waypoints
	 * @return String with the rest of the query to Google Maps
	 */
	private static String restOfQuery(List<com.pifive.makemyrun.geo.Location> waypoints) {
		StringBuilder stringBuilder = new StringBuilder("");
		Log.d("MMR", "whattafack");
		for (com.pifive.makemyrun.geo.Location waypoint : waypoints) {
			Log.d("MMR", "whattafack2");
			stringBuilder.append(waypoint.getLat());
			Log.d("MMR", ""+waypoint.getLat());
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
	 * Generates waypoints when the vertical sides are more than 5 times shorter than horizontal sides.
	 * @param northLat Northest latitude
	 * @param southLat Southest latitude
	 * @param westLong Westest longitude
	 * @param eastLong Eastest longitude
	 * @return waypoints
	 */
	private static List<com.pifive.makemyrun.geo.Location> generateHorizontalThin(
			double northLat, double southLat, double westLong, double eastLong) {
		
		List<com.pifive.makemyrun.geo.Location> locations = 
				new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		double diff = (northLat-southLat);
		for(int i=0; i<2; i++) {
			double randLat = diff * (new Random()).nextDouble() + southLat + 
					((new Random()).nextBoolean() ? -1.0 : 1.0) * diff;
			double randLong = (eastLong - westLong) * (new Random()).nextDouble() + westLong;
			locations.add(new com.pifive.makemyrun.geo.Location(randLat, randLong));
		}
		
		return null;
	}

	/**
	 * Generates waypoints when the horizontal sides are more than 5 times shorter than vertical sides.
	 * @param northLat Northest latitude
	 * @param southLat Southest latitude
	 * @param westLong Westest longitude
	 * @param eastLong Eastest longitude
	 * @return waypoints
	 */
	private static List<com.pifive.makemyrun.geo.Location> generateVerticalThin(
			double northLat, double southLat, double westLong, double eastLong) {
		
		List<com.pifive.makemyrun.geo.Location> locations = 
				new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		double diff = (eastLong-westLong);
		for(int i=0; i<2; i++) {
			double randLong = diff * (new Random()).nextDouble() + westLong + 
					((new Random()).nextBoolean() ? -1.0 : 1.0) * diff;
			double randLat = (northLat - southLat) * (new Random()).nextDouble() + southLat;
			locations.add(new com.pifive.makemyrun.geo.Location(randLat, randLong));
		}
		
		return null;
	}

	/**
	 * Generates waypoints when the circumstances are normal
	 * @param northLat Northest latitude
	 * @param southLat Southest latitude
	 * @param westLong Westest longitude
	 * @param eastLong Eastest longitude
	 * @return waypoints
	 */
	private static List<com.pifive.makemyrun.geo.Location> 
						generateNormal(
						final double northLat, final double southLat, 
						final double westLong, final double eastLong) {
		
		List<com.pifive.makemyrun.geo.Location> locations = 
				new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		for(int i=0; i<2; i++) {
			double randLat = (northLat - southLat) * (new Random()).nextDouble() + southLat;
			double randLong = (eastLong - westLong) * (new Random()).nextDouble() + westLong;
			locations.add(new com.pifive.makemyrun.geo.Location(randLat, randLong));
		}
		
		return locations;
	}
	
	/**
	 * Generates waypoints for situations where the longitudes are the same
	 * @param aLoc starting location
	 * @param bLoc finish location
	 * @return waypoints 
	 */
	private static List<com.pifive.makemyrun.geo.Location> 
						generateStraightVertical(
						final com.pifive.makemyrun.geo.Location aLoc,
			 			final com.pifive.makemyrun.geo.Location bLoc) {
		
		List<com.pifive.makemyrun.geo.Location> locations = 
				new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		double southLat = aLoc.getLat() > bLoc.getLat() ? bLoc.getLat() : aLoc.getLat();
		double westLong = aLoc.getLng();
		double eastLong = aLoc.getLng();
		
		for(int i=0; i<2; i++) {
			double randLat = ((new Random()).nextBoolean() ? -1.0 : 1.0) * 0.0005 
											* (new Random()).nextDouble() + southLat;
			double randLong = (eastLong - westLong) * (new Random()).nextDouble() + westLong;
			locations.add(new com.pifive.makemyrun.geo.Location(randLat, randLong));
		}
		
		return locations;
	}
	
	
	/**
	 * Generates waypoints for situations where the latitudes are the same
	 * @param aLoc starting location
	 * @param bLoc finish location
	 * @return waypoints 
	 */
	private static List<com.pifive.makemyrun.geo.Location> 
						generateStraightHorizontal(
						final com.pifive.makemyrun.geo.Location aLoc,
			 			final com.pifive.makemyrun.geo.Location bLoc) {
		
		List<com.pifive.makemyrun.geo.Location> locations = 
				new ArrayList<com.pifive.makemyrun.geo.Location>();
		
		double northLat = aLoc.getLat();
		double southLat = aLoc.getLat();
		double westLong = aLoc.getLng() < bLoc.getLng() ? aLoc.getLng() : bLoc.getLng();
		
		for(int i=0; i<2; i++) {
			double randLat = (northLat - southLat) * (new Random()).nextDouble() + southLat;
			double randLong = ((new Random()).nextBoolean() ? -1.0 : 1.0) * 0.0005 
									            * (new Random()).nextDouble() + westLong;
			
			locations.add(new com.pifive.makemyrun.geo.Location(randLat, randLong));
		}
		
		return locations;
	}
	
	
	
	
	/**
	 * Generates a location with coordinates 0.005 - 0.010 latitude and 
	 * longitude from the passed location
	 * @param location
	 * @return
	 */
	private static com.pifive.makemyrun.geo.Location generateRandomLocation(com.pifive.makemyrun.geo.Location location) {
		// create another location approx 0.005 - 0.010 from the current
		Random random = new Random();
		double randomNumber = 0.005 + random.nextDouble() * 0.010;
		double latSign = random.nextBoolean() ? 1.0 : -1.0;
		double longSign = random.nextBoolean() ? 1.0 : -1.0;
		
		double centerLatitude = location.getLat() + latSign*randomNumber;
		double centerLongitude = location.getLng() + longSign*randomNumber;
		
		return new com.pifive.makemyrun.geo.Location(centerLatitude, centerLongitude);
	}
	
	/**
	 * Returns a circle-shaped run
	 * @param center
	 * @param start
	 * @return
	 */
	private static List<com.pifive.makemyrun.geo.Location> getCircle(com.pifive.makemyrun.geo.Location center, 
			com.pifive.makemyrun.geo.Location start) {
		
		List<com.pifive.makemyrun.geo.Location> locations = new ArrayList<com.pifive.makemyrun.geo.Location>();
		double angle = (Math.PI * 2) / 3;
		double longDiff = start.getLng() - center.getLng();
		double latDiff = start.getLat() - center.getLat();
		double radius = Math.sqrt(Math.pow(longDiff, 2) + Math.pow(latDiff, 2));
		
		locations.add(new com.pifive.makemyrun.geo.Location(center.getLat() + Math.cos(angle)*radius, 
									   center.getLng() + Math.sin(angle)*radius));
		locations.add(new com.pifive.makemyrun.geo.Location(center.getLat() + Math.cos(angle*2)*radius, 
				   center.getLng() + Math.sin(angle*2)*radius));
		
		return locations;
	}
}
