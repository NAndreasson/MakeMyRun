package com.pifive.makemyrun.test;

import android.database.Cursor;
import android.database.SQLException;

import com.pifive.makemyrun.database.MMRDbAdapter;

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

	public void testOpen() {
		MMRDbAdapter self = null;
		try {

			self = testAdapter.open();

		} catch (SQLException e) {
			fail();
		}
		assertTrue(self == testAdapter);
	}

	public void testCreateRun() {
		int startTime = (int) (System.currentTimeMillis() / 1000) - 3000;
		int id = testAdapter.createRun("aabbccddeeff", startTime, 100, true);
		assertTrue(id != -1);
		Cursor cursor = testAdapter.fetchRun(id);
		cursor.moveToLast();
		assertTrue(cursor.getCount() > 0);
		assertEquals(id, cursor.getInt(0));
		assertEquals(startTime, cursor.getInt(2));
		assertEquals(100, cursor.getInt(4));
		assertEquals(1, cursor.getInt(5));
		int id2 = testAdapter.createRun("aabbccddeeff", startTime, 100, true);
		assertTrue(id2 > id);
	}

	public void testFetchAllRuns() {
		testAdapter.createRun("aabbccddeeff", 123123123, 100, true);
		Cursor cursor = testAdapter.fetchAllRuns();
		assertTrue(cursor.getCount() > 0);
		assertEquals(cursor.getColumnCount(), 6);
	}

	public void testFetchAllRoutes() {
		testAdapter.createRun("aabbccddeeff", 123123123, 100, true);
		Cursor cursor = testAdapter.fetchAllRoutes();
		assertTrue(cursor.getCount() > 0);
		assertEquals(cursor.getColumnCount(), 2);
	}

	public void testFetchRoute() {
		String testPoly = "abc";
		int runId = testAdapter.createRun(testPoly, 321, 1, false);
		Cursor cursorRun = testAdapter.fetchRun(runId);
		cursorRun.moveToLast();
		int routeId = cursorRun.getInt(1);
		Cursor cursorRoute = testAdapter.fetchRoute(routeId);
		cursorRoute.moveToLast();
		assertEquals(routeId, cursorRoute.getInt(0));
		assertEquals(testPoly, cursorRoute.getString(1));
	}
	
	public void testFetchAllRunsJoinRoutes(){
		testAdapter.createRun("aabbccddeeff", 123123123, 100, true);
		Cursor cursor = testAdapter.fetchAllRunsJoinRoutes();
		assertTrue(cursor.getCount() > 0);
		assertEquals(cursor.getColumnCount(), 7);
	}
}
