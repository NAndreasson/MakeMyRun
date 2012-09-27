package com.pifive.makemyrun.test;

import junit.framework.TestCase;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.pifive.makemyrun.DirectionsTask;

public class DirectionsTaskTest extends TestCase{

	private DirectionsTask task = new DirectionsTask();
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testExecution() {
		task.execute();
		assertTrue(task.getStatus() == AsyncTask.Status.RUNNING);
		
	}
	
	public void testGet() {
		try {
			task.execute();
			JSONObject json = task.get();
			assertTrue("Verify JSON exists after return by task.get()",
					json != null && json.length() > 1);
			
		} catch (Exception e) {
			fail("Failed to get DirectionsTask return value after execution," +
					e.getClass() + " was thrown");
		}
	}
	
	public void testCancelOnException() {
		task = new DirectionsTask();
		task.execute("FGSGDSF");
		try {
			JSONObject json = task.get();
			fail("Verify that trying to contact google " +
					"with invalid response throws exception");
		} catch (Exception e) {
			assertTrue(task.isCancelled());
			assertEquals("Verify that we can force a google fail response",
					com.pifive.makemyrun.R.string.google_rest_failed,
					task.getCancelCause());
		}
	}
	
	public void testMalformedURL() {
		task = new DirectionsTask();
		
		// Try to throw an URL Exception 
		task.execute(" |*.<å<äö§>§§§|.com.se.eu/.net");
		
		try {
			task.get();
			fail("Verify that we can catch malformed URLs");
			 
		} catch (Exception e) {
			assertTrue("Verify task cancelled after malformed URL", 
					task.isCancelled());
		}
	}

}
