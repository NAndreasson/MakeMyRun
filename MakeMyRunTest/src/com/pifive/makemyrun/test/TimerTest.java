package com.pifive.makemyrun.test;

import java.util.TimerTask;

import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.TextView;

import com.pifive.makemyrun.Timer;

public class TimerTest extends AndroidTestCase {
	
	public void test() {
		
		final Timer testTimer = new Timer(new TextView(getContext()));
		
		java.util.Timer javaTimer = new java.util.Timer();
		javaTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.d("MMR", "TIME NOW!: " + testTimer.getTime());
				assertEquals(2, testTimer.getTime());				
			}
		}, 2000);
	}

}
