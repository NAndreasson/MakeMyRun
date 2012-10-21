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
