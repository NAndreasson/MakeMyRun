package com.pifive.makemyrun;

import android.os.Handler;

public class Timer {
	
	Handler handler = new Handler();
	long start = 0;
	
	protected Timer() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				long millis = System.currentTimeMillis() - start;
				int seconds = (int) millis/1000;
				int min = seconds/60;
				seconds = seconds % 60;
			}
		};
	}
}
