package com.pifive.makemyrun.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.pifive.makemyrun.RouteGenerator;
import com.pifive.makemyrun.Location;

/**
 * Test class for RouteGenerator
 */
public class RouteGeneratorTest extends TestCase {
	
	/**
	 * Passes is string is returned
	 */
	public void testGenerateRoute() {
		String route = RouteGenerator.generateRoute(new Location(57.7000, 12.0000));
		assert(route != null);
		assert(route.contains("origin="));
		assert(route.contains("&destination="));
		assert(route.contains("&avoid=highways&sensor=true&mode=walking"));
	}
	
	/**
	 * Passes if:
	 * 2 � points � 10
	 * Each returned value doesn't equal the one before
	 * Number of returned PiLocations equals the number of points sent in
	 */
	public void testGetCircle() {
		Location center = new Location(57.7000, 12.000);
		Location start = new Location(57.6990, 11.990);
		
		try {
			@SuppressWarnings("unused")
			List<Location> locationsOne = RouteGenerator.getCircle(center, start, -34); // Exception
			fail("No exception");
		} catch (IllegalArgumentException e) {
			// ...
		}
		
		try {
			List<Location> locationsTwo = RouteGenerator.getCircle(center, start, 15); // Exception
			fail("No exception");
		} catch(IllegalArgumentException e) {	
			// ...
		}
		
		List<Location> workingLocations = RouteGenerator.getCircle(center, start, 10);
		
		assert(workingLocations.size() == 10);
		
		Location lastLocation = null;
		Location presentLocation = null;
		Iterator<Location> iterator = workingLocations.iterator();
		while(iterator.hasNext()) {
			if(lastLocation == null) {
				lastLocation = iterator.next();
			} else {
				presentLocation = iterator.next();
				assert(presentLocation.getLat() != lastLocation.getLat() && 
						presentLocation.getLng() != lastLocation.getLng());
			}
		}
	}
	
	/**
	 * Passes if the random location is not too far from location
	 */
	public void testGetRandomLocation() {
		Location location = new Location(68, 68);
		Location randomLocation = RouteGenerator.generateRandomLocation(location);
		assertTrue(location.getLat() <= randomLocation.getLat());
		assertTrue(location.getLng() <= randomLocation.getLng());
		assertTrue(randomLocation.getLat() >= (location.getLat() + 0.003));
		assertTrue(randomLocation.getLat() <= location.getLat() + 0.007);
		assertTrue(randomLocation.getLng() >= location.getLng() + 0.003);
		assertTrue(randomLocation.getLng() <= location.getLng() + 0.007);
	}
}
