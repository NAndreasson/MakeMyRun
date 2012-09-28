package com.pifive.makemyrun.test;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.pifive.makemyrun.PiLocation;
import com.pifive.makemyrun.RouteGenerator;

/**
 * Test class for RouteGenerator
 */
public class RouteGeneratorTest extends TestCase {
	private static Context testContext;
	
	/**
	 * Generates a MainActivity to get a context from when testing.
	 * @return MainActivity abstracted as a Context
	 */
	private Context getTestContext() {
		if (testContext == null) {
			testContext = new Activity();
		}
		return testContext;
	}
	
	/**
	 * Passes if:
	 * Return value is not null
	 * Return value is String.
	 */
	public void testPrintableCurrentLocation() {
		assert(RouteGenerator.printableCurrentLocation(getTestContext()) != null);
		assert(RouteGenerator.printableCurrentLocation(getTestContext()).getClass() == String.class);
	}
	
	/**
	 * Passes if:
	 * Latitude and longitude from own location equal latitude and longitude from method return value.
	 */
	public void testGetCurrentLocation() {
		Location returnLocation = RouteGenerator.getCurrentLocation(getTestContext());
		assert(returnLocation != null);
	}
	
	/**
	 * Passes if:
	 * 2 ² points ² 10
	 * Each returned value doesn't equal the one before
	 * Number of returned PiLocations equals the number of points sent in
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testGetCircle() {
		PiLocation center = new PiLocation(57.7000, 12.000);
		PiLocation start = new PiLocation(57.6990, 11.990);
		@SuppressWarnings("unused")
		List<PiLocation> locationsOne = RouteGenerator.getCircle(center, start, -34); // Exception
		List<PiLocation> locationsTwo = RouteGenerator.getCircle(center, start, 15); // Exception
		
		assert(locationsTwo == null); // Should be null, right?
		
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
}
