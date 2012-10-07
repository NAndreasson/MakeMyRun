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

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Path;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.maps.MapView;
import com.pifive.makemyrun.Location;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.drawing.EmptyRouteException;
import com.pifive.makemyrun.drawing.RouteArtist;

public class RouteArtistTest extends ActivityInstrumentationTestCase2<MainActivity>{

	public RouteArtistTest() {
		super(MainActivity.class);
	}

	private RouteArtist routeArtist;
	private MapView mapView;
	private List<Location> list = new LinkedList<Location>();

	/**
	 * Sets up MapView with Overlays and mocks a location list.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		mapView = (MapView) getActivity().findViewById(com.pifive.makemyrun.R.id.mapview);
		
		// Mock locations
		list.add(new Location(1.0, 2.0));
		list.add(new Location(1.1, 2.0));
	}

	/**
	 * Asserts some lightweight data on the path used by draw
	 */
	public void testDraw() {
		routeArtist = new RouteArtist(list);
		Canvas canvas = new Canvas();
		routeArtist.draw(canvas, mapView, false);
		
		Path path = routeArtist.getPath();
		assertFalse("Verify that the path drawn has content.", 
				path.isEmpty());
	}
	
	/**
	 * Asserts that we can not construct the artist with a zero size list.
	 * Expect an EmptyRouteException.
	 */
	public void testListConsistency() {
		try {
			routeArtist = new RouteArtist(new LinkedList<Location>());
			fail("Verify that we throw an exception when trying to draw an empty route");
		} catch (EmptyRouteException e) {
			assertTrue("Verify that we can catch an EmptyRouteException", 
					e.getClass() == EmptyRouteException.class);
		}
	}
}
