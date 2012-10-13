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

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
	 * Returns a string containing a google query with generated waypoints 
	 * @param startEndLoc - Current location that the generated route will start and end at
	 * @return a google query with which you can query google for more steps
	 */
	public static String generateRoute(final com.pifive.makemyrun.geo.Location startLoc, final com.pifive.makemyrun.geo.Location endLoc) {		
		// build the beginning of the google query
		StringBuilder stringBuilder = new StringBuilder("origin=");
		stringBuilder.append(startLoc.getLat());
		stringBuilder.append(",");
		stringBuilder.append(startLoc.getLng());
		stringBuilder.append("&destination=");
		stringBuilder.append(endLoc.getLat());
		stringBuilder.append(",");
		stringBuilder.append(endLoc.getLng());
		stringBuilder.append("&waypoints=");
		
		// get the centerpoint of the 'circle'
		com.pifive.makemyrun.geo.Location centerLocation = generateRandomLocation(startLoc);
		List<com.pifive.makemyrun.geo.Location> waypoints = getCircle(centerLocation, startLoc, 6);
		for (com.pifive.makemyrun.geo.Location waypoint : waypoints) {
			stringBuilder.append(waypoint.getLat());
			stringBuilder.append(",");
			stringBuilder.append(waypoint.getLng());
			stringBuilder.append("|");
		}
		// remove the last |
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("&avoid=highways&sensor=true&mode=walking");
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}
	
	/**
	 * Generates a route when a starting point and finish point is given
	 * @param aLoc starting point
	 * @param bLoc finish point
	 */
	public static void generateRoute(Location aLoc, Location bLoc) {
		// Distance between locations in degrees
		double distance = Math.sqrt(Math.pow(aLoc.getLatitude() - bLoc.getLatitude(), 2) 
				+ Math.pow(aLoc.getLongitude() - bLoc.getLongitude(), 2));
		
	}
	
	
	/**
	 * Generates a location with coordinates 0.003 - 0.007 latitude and 
	 * longitude from the passed location
	 * @param location
	 * @return
	 */
	public static com.pifive.makemyrun.geo.Location generateRandomLocation(com.pifive.makemyrun.geo.Location location) {
		// create another location approx 0.003 - 0.007 from the current
		Random random = new Random();
		double randomNumber = 0.003 + random.nextDouble() * 0.004;
		double latSign = random.nextBoolean() ? 1.0 : -1.0;
		double longSign = random.nextBoolean() ? 1.0 : -1.0;
		
		double centerLatitude = location.getLat() + latSign*randomNumber;
		double centerLongitude = location.getLng() + longSign*randomNumber;
		
		return new com.pifive.makemyrun.geo.Location(centerLatitude, centerLongitude);
	}
	
	/**
	 * Returns X number of points in a circle, all with the same distance from center.
	 */
	public static List<com.pifive.makemyrun.geo.Location> getCircle(com.pifive.makemyrun.geo.Location center, 
			com.pifive.makemyrun.geo.Location start, int points) {
		if (points < 2) {
			throw new IllegalArgumentException("At least two points are needed");
		} else if (points > 10) {
			throw new IllegalArgumentException("No more than 10 points");
		}
		
		List<com.pifive.makemyrun.geo.Location> locations = new ArrayList<com.pifive.makemyrun.geo.Location>();
		double angle = (Math.PI * 2) / (double) points;
		double longDiff = start.getLng() - center.getLng();
		double latDiff = start.getLat() - center.getLat();
		double radius = Math.sqrt(Math.pow(longDiff, 2) + Math.pow(latDiff, 2));
		
		for(int i=1; i<points; i++) {
			locations.add(new com.pifive.makemyrun.geo.Location(center.getLat() + Math.cos(angle*i)*radius, 
									   center.getLng() + Math.sin(angle*i)*radius));
		}
		
		return locations;
	}
}
