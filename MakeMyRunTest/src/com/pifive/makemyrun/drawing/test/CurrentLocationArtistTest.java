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

package com.pifive.makemyrun.drawing.test;

import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.drawing.CurrentLocationArtist;
import com.pifive.makemyrun.drawing.Drawer;

/**
 * Test file for CurrentLocationArtist
 */
public class CurrentLocationArtistTest extends ActivityInstrumentationTestCase2<MainActivity>{
	private double micro = 1E6;
	private double testDegree = 15.0;
	private double testDegree2 = 15.00001;
	private double testDegree3 = 100;
	private double testDegree4 = 50;
	private int testAccuracy = 50;
	private int testAccuracy2 = 1;
	
	public CurrentLocationArtistTest() {
		super(MainActivity.class);
	}

	private Location location = new Location(LocationManager.GPS_PROVIDER);
	private MockDrawer drawer = new MockDrawer();
	private CurrentLocationArtist artist;
	private MapView mapView;
	
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail();
		}
		
		location.setLatitude(testDegree);
		location.setLongitude(testDegree);
		
		artist = new CurrentLocationArtist(drawer);
		artist.onLocationChanged(location);
		
		// We don't need to test with correct key 
		// since we don't need to draw the actual map
		mapView = (MapView) getActivity().
						findViewById(com.pifive.makemyrun.R.id.mapview);
	}
		
	/**
	 * Tests that we can convert between geopoints and locations without precision loss
	 */
	public void testToGeoPoint() {
		GeoPoint geoPoint = CurrentLocationArtist.toGeoPoint(location);
		assertEquals("Verify that the point converted is true to " +
						"the format in latitude", 
				geoPoint.getLatitudeE6() / micro, location.getLatitude());
		assertEquals("Verify that the point converted is true " +
						"to the format in longitude", 
				geoPoint.getLongitudeE6() / micro, location.getLongitude());
	}
	
	/**
	 * Tests that a locationupdate with location.accuracy <= distance(oldloc -> newloc)
	 */
	public void testLocationUpdateRequestsDraw() {
		// Reset request status
		drawer.isDrawRequested();
		
		// Test an update which shall not render a redraw
		Location newLoc = new Location(LocationManager.GPS_PROVIDER);
		newLoc.setAccuracy(testAccuracy);
		newLoc.setLatitude(testDegree2);
		newLoc.setLongitude(testDegree2);
		artist.onLocationChanged(newLoc);
		assertEquals("Verify that we do not update and redraw on bad location",
				false, drawer.isDrawRequested());
		
		// Test an update which must render a redraw
		newLoc.setAccuracy(testAccuracy2);
		newLoc.setLatitude(testDegree3);
		newLoc.setLongitude(testDegree3);
		artist.onLocationChanged(newLoc);
		assertEquals("Verify that we DO update and redraw on acceptable location",
				true, drawer.isDrawRequested());
	}
	
	/**
	 * Verifies point being drawn in a simple way
	 */
	public void testPointUpdate() {
		Point point = artist.getPoint();
		
		// Change location
		location.setLatitude(testDegree4);
		location.setLongitude(testDegree4);
		artist.onLocationChanged(location);

		// Draw and assert changed screen position
		artist.draw(new Canvas(), mapView, false);
		assertTrue("Verify that we have a point with different x and y values after draw" +
				" and after locationchanged",
				point.x != artist.getPoint().x && 
				point.y != artist.getPoint().y);
	}
	
	/**
	 * Mocked Drawer only for testing if redraw has been called.
	 */
	private class MockDrawer implements Drawer {
		
		private boolean drawRequested = false;
		
		/**
		 * Returns true if the reDraw() methofd has been called since
		 * the last call on this method. Which then also resets request status.
		 * @return Returns true if reDraw() has been called since last call of this method.
		 */
		protected boolean isDrawRequested() {
			boolean returnVal = drawRequested;
			drawRequested = false;
			return returnVal;
		}

		@Override
		public void reDraw() {
			drawRequested = true;
		}
		
	}
	
}
