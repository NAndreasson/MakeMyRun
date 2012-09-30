package com.pifive.makemyrun.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.pifive.makemyrun.PiLocation;
import com.pifive.makemyrun.RouteGenerator;

/**
 * Test class for RouteGenerator
 */
public class RouteGeneratorTest extends TestCase {
	/**
	 * Passes if:
	 * 2 ² points ² 10
	 * Each returned value doesn't equal the one before
	 * Number of returned PiLocations equals the number of points sent in
	 */
	public void testGetCircle() {
		PiLocation center = new PiLocation(57.7000, 12.000);
		PiLocation start = new PiLocation(57.6990, 11.990);
		
		try {
			@SuppressWarnings("unused")
			List<PiLocation> locationsOne = RouteGenerator.getCircle(center, start, -34); // Exception
			fail("No exception");
		} catch (IllegalArgumentException e) {
			// ...
		}
		
		try {
			List<PiLocation> locationsTwo = RouteGenerator.getCircle(center, start, 15); // Exception
			fail("No exception");
		} catch(IllegalArgumentException e) {	
			// ...
		}
		
		List<PiLocation> workingLocations = RouteGenerator.getCircle(center, start, 10);
		
		assert(workingLocations.size() == 10);
		
		PiLocation lastLocation = null;
		PiLocation presentLocation = null;
		Iterator<PiLocation> iterator = workingLocations.iterator();
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
		PiLocation location = new PiLocation(68, 68);
		PiLocation randomLocation = RouteGenerator.generateRandomLocation(location);
		assertTrue(location.getLat() <= randomLocation.getLat());
		assertTrue(location.getLng() <= randomLocation.getLng());
		assertTrue(randomLocation.getLat() >= (location.getLat() + 0.003));
		assertTrue(randomLocation.getLat() <= location.getLat() + 0.007);
		assertTrue(randomLocation.getLng() >= location.getLng() + 0.003);
		assertTrue(randomLocation.getLng() <= location.getLng() + 0.007);
	}
}
