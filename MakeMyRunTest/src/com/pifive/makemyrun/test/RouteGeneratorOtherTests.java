package com.pifive.makemyrun.test;

import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.NoLocationException;
import com.pifive.makemyrun.RouteGenerator;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

public class RouteGeneratorOtherTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public RouteGeneratorOtherTests() {
		super(MainActivity.class);
		System.out.println("hello?!?!");
	}
	
	/**
	 * Should throw exception due to no location available in tests
	 */
	public void testGetCurrentRoute() {
		try {
			Location returnedLocation = RouteGenerator.getCurrentLocation(getActivity().getBaseContext());
			fail("No exception = fail");
		} catch (NoLocationException e) {
			assert(true);
		}
		
	}

}
