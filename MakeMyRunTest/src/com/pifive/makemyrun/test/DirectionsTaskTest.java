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

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.test.AndroidTestCase;

import com.pifive.makemyrun.DirectionsException;
import com.pifive.makemyrun.DirectionsTask;
import com.pifive.makemyrun.LoadingStatus;

public class DirectionsTaskTest extends AndroidTestCase {

	private DirectionsTask task = new DirectionsTask(getContext(),
			DirectionsTask.GOOGLE_URL);

	/**
	 * Tests asyncTask execution isolated
	 */
	public void testExecution() {
		task.execute();
		assertTrue(task.getStatus() == AsyncTask.Status.RUNNING);

	}

	/**
	 * Tests that our loadingStatus is set and used.
	 */
	public void testSetLoadingStauts() {
		LoadingStatus status = new LoadingStatus(getContext());
		task.setLoadingStatus(status);
		task.simpleGet(DirectionsTask.TEST_QUERY);
		assertTrue(!status.getMessage().equals(""));
	}
	
	/**
	 * Tests asyncTask's get() isolated
	 */
	public void testGet() {
		try {
			task.execute();
			JSONObject json = task.get();
			assertTrue("Verify JSON exists after return by task.get()",
					json != null && json.length() > 1);

		} catch (Exception e) {
			fail("Failed to get DirectionsTask return value after execution,"
					+ e.getClass() + " was thrown");
		}
	}

	/**
	 * Test abstraction method simpleGet()
	 */
	public void testSimpleGet() {
		task = new DirectionsTask(getContext(), DirectionsTask.GOOGLE_URL);
		JSONObject json = task.simpleGet(DirectionsTask.TEST_QUERY);
		try {
			assertEquals("Verify that we received correct status message",
					DirectionsTask.GOOGLE_QUERY_SUCCESS,
					json.getString("status"));
		} catch (JSONException e) {
			fail("Test could not reach json response status");
		}
	}

	/**
	 * Make sure we can cancel a task
	 */
	public void testCancelOnException() {
		task = new DirectionsTask(getContext(), DirectionsTask.GOOGLE_URL);
		task.simpleGet("MockQueryThatShouldReturnNothingOfValue");
		try {
			assertTrue("Verify that trying to contact google "
					+ "with invalid response sets error message to user",
					task.isCancelled());

			assertEquals("Verify that we can force a google fail response",
					com.pifive.makemyrun.R.string.google_rest_failed,
					task.getCancelCause());
		} catch (Exception e) {
		}
	}

	/**
	 * Make sure we can print a url_format_failed message (I.E. make sure
	 * malformed URL does not crash on us)
	 */
	public void testMalformedURL() {
		task = new DirectionsTask(getContext(), "\345DFSB://google.com?");
		task.simpleGet("DSFDS");

		// Try to throw an URL Exception
		assertTrue("Verify task cancelled after malformed URL",
				task.isCancelled());
		assertEquals("Verify that we can catch malformed URLs",
				com.pifive.makemyrun.R.string.url_format_failed,
				task.getCancelCause());
	}

	/**
	 * Test that we do not die on JSONException when parsing from string
	 */
	public void testJSONExceptionFromString() {
		task = new DirectionsTask(getContext(), DirectionsTask.GOOGLE_URL);

		try {
			task.parseJSONString("ThisAin't no valid JSON");
			fail("parseJSONString() did not handle invalid JSON with DirectionsException");
		} catch (DirectionsException e) {
			assertTrue("JSONException converted to DirectionsException", true);
		}
	}
}
