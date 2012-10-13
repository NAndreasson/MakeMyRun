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

		assertTrue(System.currentTimeMillis()/1000 - testTimer.getStartTime() > 0);
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
