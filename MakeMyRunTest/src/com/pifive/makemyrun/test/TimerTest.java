package com.pifive.makemyrun.test;

import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.TextView;

import com.pifive.makemyrun.Timer;

public class TimerTest extends AndroidTestCase {
	
	public void test() {
		Timer timer = new Timer(new TextView(getContext()));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.d("MMR", "Time: " + timer.getTime());
		assertTrue(timer.getTime() == 2);
	}

}
