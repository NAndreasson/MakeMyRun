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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.drawing.AbstractOverlayArtist;
import com.pifive.makemyrun.drawing.MapDrawer;

public class RouteDrawerTest extends ActivityInstrumentationTestCase2<MainActivity>{

	public RouteDrawerTest() {
		super(MainActivity.class);
	}

	private MapDrawer drawer;
	private MapView mapView;
	private List<Overlay> overlays;

	/**
	 * Sets up MapView with Overlays and mocks a location list.
	 */
	@Override
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("setUp failed");
		}
		
		mapView = (MapView) getActivity().findViewById(com.pifive.makemyrun.R.id.mapview);
		overlays = mapView.getOverlays();
	}
	
	/**
	 * Tests that provided mapview gets a new overlay and that center is set from
	 * starting location for the list provided.
	 */
	public void testOverlayAdding() {
		drawer = new MapDrawer(mapView);
		Overlay myOverlay = overlays.get(overlays.size() - 1);

		assertEquals("Verify that we have added a RouteOverlay after construction", 
				"class com.pifive.makemyrun.drawing.MapDrawer$MapOverlay",
				myOverlay.getClass().toString());
	}

	/**
	 * Make sure we can add an artist and that its draw method can get called.
	 */
	public void testArtistAdd() {
		drawer = new MapDrawer(mapView);
		final MockArtist artist = new MockArtist();
		drawer.addArtist(artist);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Log.d("MMRTest", "shouldbedrawn");
				assertEquals("Verify that the artist gets drawn instantly after addArtist()", 
						true, artist.isDrawn());				
			}
			
		}, 750);
	}
	
	/**
	 * Test that we have a way of completely halt all drawing
	 * and prevent the drawer from resuming drawing on the map
	 * until new artists are added.
	 */
	public void testClear() {
		drawer = new MapDrawer(mapView);
		for (int i = 0; i<5; i++) {
			drawer.addArtist(new MockArtist());
		}
		int expectedSize = overlays.size() - 1;
		drawer.clearDrawer();
		assertEquals("Verify that we can remove the drawer's overlay" +
					" and artists" +
					" from our MapView",
					expectedSize, overlays.size());
	}
	
	/**
	 * Test that after we remove the last artist the overlay is detached
	 * from the MapView, we do not wish to clutter it.
	 */
	public void testArtistRemove() {
		overlays.clear();
		
		drawer = new MapDrawer(mapView);
		
		MockArtist artist1 = new MockArtist();
		MockArtist artist2 = new MockArtist();
		
		drawer.addArtist(artist1);
		drawer.addArtist(artist2);
		
		drawer.removeArtist(artist1);
		
		assertEquals("Verify that we did not detach our overlay when " +
						"our drawer still has an artist", 
						1, overlays.size());
		
		drawer.removeArtist(artist2);
		
		assertEquals("Verify that overlay was detached after " +
						"last artist was removed",
						0, overlays.size());
	}

	/**
	 * Mocked Artist which does nothing on draw.
	 */
	private class MockArtist extends AbstractOverlayArtist {
		
		private boolean drawn = false;
		/**
		 * Returns whether this.draw has been called since last isDrawn()
		 */
		protected boolean isDrawn() {
			boolean returnVal = drawn;
			drawn = false;
			return returnVal;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			Log.d("MMRTest", "drawn");
			drawn = true;
		}
	}
}
