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

package com.pifive.makemyrun.test;

import com.google.android.maps.GeoPoint;
import com.pifive.makemyrun.model.RouteGenerator;

import android.test.AndroidTestCase;

/**
 * Test class for RouteGenerator
 */
public class RouteGeneratorTest extends AndroidTestCase {
	
	/**
	 * Passes if not null and of type String
	 */
	public void testRouteExistenceAndType() {
		GeoPoint pointOne = new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6));
		GeoPoint pointTwo = new GeoPoint((int)(57.7010 * 1E6), (int)(12.0010 * 1E6));
		
		String routeWithQuery = RouteGenerator.generateRoute(pointOne, pointTwo);
		assertNotNull(routeWithQuery);
		assertTrue(routeWithQuery.getClass() == String.class);
	}
	
	/**
	 * Passes if destination and origin equals
	 */
	public void testCircleRoute() {
		GeoPoint pointOne = new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6));
		GeoPoint pointTwo = new GeoPoint((int)(57.7003 * 1E6), (int)(12.0003 * 1E6));
		
		String routeWithQuery = RouteGenerator.generateRoute(pointOne, pointTwo);
		
		int indexOfOrigin = routeWithQuery.indexOf("=");
		int indexOfComma = routeWithQuery.indexOf(",");
		int indexOfAnd = routeWithQuery.indexOf("&");
		String otherHalf = routeWithQuery.substring(routeWithQuery.indexOf("destination="));
		int indexOfDestination = otherHalf.indexOf("=");
		int indexOfSecondComma = otherHalf.indexOf(",");
		int indexOfSecondAnd = otherHalf.indexOf("&");
		String originLat = routeWithQuery.substring(indexOfOrigin+1, indexOfComma);
		String originLong = routeWithQuery.substring(indexOfComma+1, indexOfAnd);
		String destLat = otherHalf.substring(indexOfDestination+1, indexOfSecondComma);
		String destLong = otherHalf.substring(indexOfSecondComma+1, indexOfSecondAnd);
		
		assertTrue(originLat.equals(destLat) && originLong.equals(originLong));
	}
	
	public void testDifferentRoutes() {
		testStraight(new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6)), 
				new GeoPoint((int)(57.7 * 1E6), (int)(12.01 * 1E6)));
		testStraight(new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6)), 
				new GeoPoint((int)(57.71 * 1E6), (int)(12 * 1E6)));
		testStraight(new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6)), 
				new GeoPoint((int)(57.715 * 1E6), (int)(12.02 * 1E6)));
		testStraight(new GeoPoint((int)(57.7 * 1E6), (int)(12 * 1E6)), 
				new GeoPoint((int)(57.702 * 1E6), (int)(12.5 * 1E6)));
	}
	
	/**
	 * Passes if returned waypoints doesn't equal sent in locations
	 */
	private void testStraight(GeoPoint pointOne, GeoPoint pointTwo) {
		String routeWithQuery = RouteGenerator.generateRoute(pointOne, pointTwo);
		
		String waypoints = routeWithQuery.substring(routeWithQuery.indexOf("optimize:true|") + 
													 "optimize:true|".length());
		String firstLat = waypoints.substring(0, waypoints.indexOf(","));
		String firstLong = waypoints.substring(waypoints.indexOf(",")+1, waypoints.indexOf("|"));
		
		System.out.println(routeWithQuery);
		
		assertTrue("Verifies the first waypoint lat != long", !firstLat.equals(firstLong));
		assertFalse("Verifies first waypoint is not the same as origin", 
				String.valueOf(pointOne.getLatitudeE6() / 1E6).equals(firstLat) 
				&& String.valueOf(pointOne.getLongitudeE6() / 1E6).equals(firstLong));
		
		assertFalse("Verifies first waypoint is not the same as destination", 
				String.valueOf(pointTwo.getLatitudeE6() / 1E6).equals(firstLat) 
				&& String.valueOf(pointTwo.getLongitudeE6() / 1E6).equals(firstLong));
		
		String waypoints2 = waypoints.substring(waypoints.indexOf("|")+1);
		String secondLat = waypoints2.substring(0, waypoints2.indexOf(","));
		String secondLong = waypoints2.substring(waypoints2.indexOf(",")+1, waypoints2.indexOf("&"));
		
		assertTrue("Verifies the first waypoint lat != long", !secondLat.equals(secondLong));
		assertFalse("Verifies first waypoint is not the same as origin", 
				String.valueOf(pointOne.getLatitudeE6() / 1E6).equals(secondLat) 
				&& String.valueOf(pointOne.getLongitudeE6() / 1E6).equals(secondLong));
		assertFalse("Verifies first waypoint is not the same as destination", 
				String.valueOf(pointTwo.getLatitudeE6() / 1E6).equals(secondLat) 
				&& String.valueOf(pointTwo.getLongitudeE6() / 1E6).equals(secondLong));
		
	}
}