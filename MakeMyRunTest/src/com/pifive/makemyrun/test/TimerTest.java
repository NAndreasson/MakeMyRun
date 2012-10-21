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

import android.test.AndroidTestCase;
import android.widget.TextView;

import com.pifive.makemyrun.Timer;

public class TimerTest extends AndroidTestCase {

	Timer testTimer;

	/**
	 * 
	 */
	public void testGetStartTime(){
		testTimer = new Timer(new TextView(getContext()));
		testTimer.start();

		assertTrue("verifies that startTime was less than 2 seconds ago"
				,testTimer.getStartTime()-(System.currentTimeMillis()-2000) > 0);
	}
	/**
	 * Tests that we can start a timer
	 */
	public void testStartAndRun() {
		testTimer = new Timer(new TextView(getContext()));
		testTimer.start();

		assertEquals("Verify that a started timer is running",
				true, testTimer.isRunning());
	}

	/**
	 * Test that we can stop the timer running.
	 */
	public void testStop() {
		testTimer = new Timer(new TextView(getContext()));
		testTimer.start();
		testTimer.stop();
		
		assertEquals("Verify that a stopped timer is not running anymore",
				false, testTimer.isRunning());

	}

}
