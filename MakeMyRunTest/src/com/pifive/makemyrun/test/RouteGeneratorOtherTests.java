package com.pifive.makemyrun.test;

import android.location.Location;
import android.test.AndroidTestCase;

import com.pifive.makemyrun.NoLocationException;
import com.pifive.makemyrun.RouteGenerator;

public class RouteGeneratorOtherTests extends
		AndroidTestCase {
	
	/**
	 * Should throw exception due to no location available in tests
	 */
	public void testGetCurrentRoute() {
		try {
			Location returnedLocation = RouteGenerator.getCurrentLocation(getContext());
			fail("No exception = fail");
		} catch (NoLocationException e) {
			assert(true);
		}
		
	}

}
