package com.pifive.makemyrun.test;

import android.database.Cursor;
import android.database.SQLException;

import com.pifive.makemyrun.database.MMRDbAdapter;

public class MMRDbAdapterTest extends android.test.AndroidTestCase{

	private MMRDbAdapter testAdapter ; 
	
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
	
	public void testOpen(){
		MMRDbAdapter self = null;
		try {

			self = testAdapter.open();
			
		} catch(SQLException e){
			fail();
		}
		assertTrue(self == testAdapter);
	}
	
	public void testCreateRun(){
		int startTime = (int)(System.currentTimeMillis()/1000)-3000;
		int id = testAdapter.createRun("aabbccddeeff", startTime, 100, true);
		assertTrue(id != -1);
		Cursor cursor = testAdapter.fetchRun(id);
		cursor.moveToLast();
		assertTrue(cursor.getCount() > 0);
		assertEquals(id, cursor.getInt(0));
		assertEquals(startTime, cursor.getInt(2)); 
		assertEquals(100,cursor.getInt(4)); 
		assertEquals(1,cursor.getInt(5));
		int id2 = testAdapter.createRun("aabbccddeeff", startTime, 100, true);
		assertTrue(id2 > id);
	}
	
	public void testFetchAllRuns(){
		Cursor cursor = testAdapter.fetchAllRuns();
		
	}
	
}
