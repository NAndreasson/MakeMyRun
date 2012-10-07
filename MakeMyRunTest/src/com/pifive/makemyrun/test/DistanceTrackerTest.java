package com.pifive.makemyrun.test;

import com.pifive.makemyrun.DistanceTracker;
import android.location.Location;
import android.test.AndroidTestCase;

public class DistanceTrackerTest extends AndroidTestCase {
	
	public void testOnLocationChanged() {
		Location startLocation = new Location("Test");
		startLocation.setLatitude(57.717509);
		startLocation.setLongitude(12.015867);
		DistanceTracker distanceTracker = new DistanceTracker(startLocation);
		assertEquals(true, distanceTracker.getTotalDistanceInMeters() == 0);
		
		Location secondLocation = new Location("Test");
		secondLocation.setLatitude(57.717563);
		secondLocation.setLongitude(12.016361);
		secondLocation.setAccuracy(50);
		distanceTracker.onLocationChanged(secondLocation);
		assertEquals(true, distanceTracker.getTotalDistanceInMeters() == 0);
				
		Location thirdLocation = new Location("Test");
		thirdLocation.setLatitude(57.71778);
		thirdLocation.setLongitude(12.017455);
		thirdLocation.setAccuracy(50);
		distanceTracker.onLocationChanged(thirdLocation);
		assertEquals(true, distanceTracker.getTotalDistanceInMeters() == startLocation.distanceTo(thirdLocation));
	}	
}
