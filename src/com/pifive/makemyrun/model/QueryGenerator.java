/**
 * 
 */
package com.pifive.makemyrun.model;

import java.util.List;

/**
 * QueryGenerator
 * Class for generating queries to Google.
 *
 */
public class QueryGenerator {
	
	/**
	 * Private constructor to prevent from being instantiated
	 */
	private QueryGenerator() {
		
	}
	
	/**
	 * Generates a query that we can send to Google
	 * @param startLoc Starting location
	 * @param stopLoc Finish location
	 * @param waypoints Way points on the way
	 * @return a google ready query
	 */
	public static String googleQuery(final com.pifive.makemyrun.geo.MMRLocation startLoc,
									 final com.pifive.makemyrun.geo.MMRLocation stopLoc,
									 final List<com.pifive.makemyrun.geo.MMRLocation> waypoints) {
		return startOfQuery(startLoc, stopLoc).concat(restOfQuery(waypoints));
	}
	
	/**
	 * Generates the start of the google query
	 * @param aLoc
	 * @param bLoc
	 * @return
	 */
	private static String startOfQuery(final com.pifive.makemyrun.geo.MMRLocation aLoc,
			 						   final com.pifive.makemyrun.geo.MMRLocation bLoc) {
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
	private static String restOfQuery(List<com.pifive.makemyrun.geo.MMRLocation> waypoints) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (com.pifive.makemyrun.geo.MMRLocation waypoint : waypoints) {
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
}
