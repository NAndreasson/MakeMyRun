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

import android.database.Cursor;
import android.database.SQLException;

import com.pifive.makemyrun.database.MMRDbAdapter;

/**
 * Test cases for MMRDbAdapter
 * 
 */
public class MMRDbAdapterTest extends android.test.AndroidTestCase {

	private MMRDbAdapter testAdapter;

	@Override
	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail();
		}

		testAdapter = new MMRDbAdapter(this.getContext());
		testAdapter.open();
	}

	@Override
	protected void tearDown() {
		try {
			super.tearDown();
		} catch (Exception e) {
			fail();
		}

		testAdapter.close();
	}

	/**
	 * Tries to open the database, If it fails a db cant be opened or created
	 */
	public void testOpen() {
		MMRDbAdapter self = null;
		try {

			self = testAdapter.open();

		} catch (SQLException e) {
			fail();
		}
		assertTrue(self == testAdapter);
	}

	/**
	 * Inserts a row into runs table and make sure that it's values are correct
	 * Also makes sure that the id is auto incrementing
	 */
	public void testCreateRun() {
		int distanceRan = 100;
		int startTime = (int) (System.currentTimeMillis() / 1000) - 3000;
		int id = testAdapter.createRun("aabbccddeeff", startTime, distanceRan, 0,
				true);
		assertTrue(id != -1);
		Cursor cursor = testAdapter.fetchRun(id);
		cursor.moveToLast();
		assertTrue(cursor.getCount() > 0);
		assertEquals(id, cursor.getInt(0));
		assertEquals(startTime, cursor.getInt(2));
		assertEquals(distanceRan, cursor.getInt(4));
		assertEquals("verifies that run was completed",1, cursor.getInt(5));
		int id2 = testAdapter.createRun("aabbccddeeff", 0, 0, 0,
				true);
		assertTrue(id2 > id);
	}

	/**
	 * Adds a run and makes sure that it the resulting cursor has multiple rows
	 */
	public void testFetchAllRuns() {
		testAdapter.createRun("aabbccddeeff", 0, 0, 0, true);
		testAdapter.createRun("aabbccddeeff", 0, 0, 0, true);
		Cursor cursor = testAdapter.fetchAllRuns();
		assertTrue(cursor.getCount() > 1);
		assertEquals(cursor.getColumnCount(), 6);
	}

	/**
	 * Fetches all routes makes sure that the curser hold multiple rows
	 */
	public void testFetchAllRoutes() {
		testAdapter.createRun("aabbccddeeff", 0, 0, 0, true);
		testAdapter.createRun("aabbsfdaffdf", 0, 0, 0, true);
		Cursor cursor = testAdapter.fetchAllRoutes();
		assertTrue(cursor.getCount() > 1);
		assertEquals(cursor.getColumnCount(), 3);
	}

	/**
	 * Makes sure that only a single row is returned with the correct values.
	 */
	public void testFetchRun() {
		int distanceRan = 1;
		int runId = testAdapter.createRun("abc", 0, distanceRan, 0, false);

		Cursor cursor = testAdapter.fetchRun(runId);
		assertEquals(cursor.getCount(), 1);
		assertEquals(cursor.getColumnCount(), 6);
		cursor.moveToFirst();
		assertEquals(distanceRan, cursor.getInt(cursor
				.getColumnIndex(MMRDbAdapter.KEY_RUN_DISTANCE_RAN)));
		assertEquals(0, cursor.getInt(5));
	}

	/**
	 * Makes sure that only a single row is returned with the correct data
	 */
	public void testFetchRoute() {
		String testPoly = "abc";
		int runId = testAdapter.createRun(testPoly, 0, 0, 0, false);
		Cursor cursorRun = testAdapter.fetchRun(runId);
		cursorRun.moveToLast();
		int routeId = cursorRun.getInt(1);
		Cursor cursorRoute = testAdapter.fetchRoute(routeId);
		cursorRoute.moveToLast();
		assertEquals(routeId, cursorRoute.getInt(0));
		assertEquals(testPoly, cursorRoute.getString(1));
	}

	/**
	 * Make sure that we got all the columns joined together
	 */
	public void testFetchAllRunsJoinRoutes() {
		testAdapter.createRun("aabbccddeeff", 0, 0, 0, true);
		Cursor cursor = testAdapter.fetchAllRunsJoinRoutes();
		assertTrue(cursor.getCount() > 0);

		assertEquals(cursor.getColumnCount(), 8);
	}

	/**
	 * Make sure that our row has all columns and that it holds the data we want
	 */
	public void testFetchRunsJoinRoute() {
		String testPoly = "aabbccddeeff";
		int runId = testAdapter.createRun(testPoly, 0, 0, 0, true);

		Cursor cursor = testAdapter.fetchRunJoinRoute(runId);
		assertEquals(cursor.getColumnCount(), 8);
		assertEquals(cursor.getCount(), 1);
		cursor.moveToFirst();
		assertEquals(cursor.getString(6), testPoly);
	}

	/**
	 * Tests to delete a run and checks that if no other run is referring this
	 * run's route, that its route also is deleted !
	 */
	public void testDeleteRun() {
		String testPoly = "thisisapolyline"; // this polyline must be unique for
												// our db!
		int runId = testAdapter.createRun(testPoly, 0, 0, 0, true);
		// create another one to have two runs referring to the same route
		int runId2 = testAdapter.createRun(testPoly, 0, 0, 0, false);
		Cursor runCursor = testAdapter.fetchRun(runId);
		runCursor.moveToFirst();
		int routeId = runCursor.getInt(runCursor
				.getColumnIndex(MMRDbAdapter.KEY_RUN_ROUTE));
		Cursor routeCursor = testAdapter.fetchRoute(routeId);
		assertEquals(1, routeCursor.getCount());
		// we delete the one now and check that the route still exsist
		assertTrue(testAdapter.deleteRun(runId));
		routeCursor = testAdapter.fetchRoute(routeId);
		assertEquals(1, routeCursor.getCount());
		// we delete the other run and check that the route was removed aswell
		assertTrue(testAdapter.deleteRun(runId2));
		routeCursor = testAdapter.fetchRoute(routeId);
		assertEquals(
				"verifies that the route is deletedsince theres no runs associated to it",
				0, routeCursor.getCount());

	}
}
