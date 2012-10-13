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
	protected void setUp() throws Exception {
		super.setUp();

		testAdapter = new MMRDbAdapter(this.getContext());
		testAdapter.open();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

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
		int startTime = (int) (System.currentTimeMillis() / 1000) - 3000;
		int id = testAdapter.createRun("aabbccddeeff", startTime, 100, 150,
				true);
		assertTrue(id != -1);
		Cursor cursor = testAdapter.fetchRun(id);
		cursor.moveToLast();
		assertTrue(cursor.getCount() > 0);
		assertEquals(id, cursor.getInt(0));
		assertEquals(startTime, cursor.getInt(2));
		assertEquals(100, cursor.getInt(4));
		assertEquals(1, cursor.getInt(5));
		int id2 = testAdapter.createRun("aabbccddeeff", startTime, 100, 150,
				true);
		assertTrue(id2 > id);
	}

	/**
	 * Adds a run and makes sure that it the resulting cursor has multiple rows
	 */
	public void testFetchAllRuns() {
		testAdapter.createRun("aabbccddeeff", 123123123, 100, 150, true);
		testAdapter.createRun("aabbccddeeff", 123123123, 100, 150, true);
		Cursor cursor = testAdapter.fetchAllRuns();
		assertTrue(cursor.getCount() > 1);
		assertEquals(cursor.getColumnCount(), 6);
	}

	/**
	 * Fetches all routes makes sure that the curser hold multiple rows
	 */
	public void testFetchAllRoutes() {
		testAdapter.createRun("aabbccddeeff", 123123123, 100, 150, true);
		testAdapter.createRun("aabbsfdaffdf", 123123123, 100, 150, true);
		Cursor cursor = testAdapter.fetchAllRoutes();
		assertTrue(cursor.getCount() > 1);
		assertEquals(cursor.getColumnCount(), 3);
	}
	
	/**
	 * Makes sure that only a single row is returned with the correct values.
	 */
	public void testFetchRun(){
		int runId = testAdapter.createRun("abc", 321, 1, 150, false);
		
		Cursor cursor = testAdapter.fetchRun(runId);
		assertEquals(cursor.getCount(),1);
		assertEquals(cursor.getColumnCount(), 6);
		cursor.moveToFirst();
		assertEquals(1,cursor.getInt(cursor.getColumnIndex(MMRDbAdapter.KEY_RUN_DISTANCE_RAN)));
		assertEquals(0,cursor.getInt(5));
	}

	/**
	 * Makes sure that only a single row is returned with the correct data
	 */
	public void testFetchRoute() {
		String testPoly = "abc";
		int runId = testAdapter.createRun(testPoly, 321, 1, 150, false);
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
		testAdapter.createRun("aabbccddeeff", 123123123, 100, 150, true);
		Cursor cursor = testAdapter.fetchAllRunsJoinRoutes();
		assertTrue(cursor.getCount() > 0);
		
		assertEquals(cursor.getColumnCount(), 8);
	}

	/**
	 * Make sure that our row has all columns and that it holds the data we want
	 */
	public void testFetchRunsJoinRoute() {
		String testPoly = "aabbccddeeff";
		int runId = testAdapter.createRun(testPoly, 123123123, 100, 150, true);
		
		Cursor cursor = testAdapter.fetchRunJoinRoute(runId);
		assertEquals(cursor.getColumnCount(), 8);
		assertEquals(cursor.getCount(), 1);
		cursor.moveToFirst();
		assertEquals(cursor.getString(6), testPoly);
	}
}
