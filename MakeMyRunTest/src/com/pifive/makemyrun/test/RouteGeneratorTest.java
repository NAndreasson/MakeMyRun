package com.pifive.makemyrun.test;

import junit.framework.TestCase;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.test.IsolatedContext;

import com.pifive.makemyrun.MainActivity;
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
}
