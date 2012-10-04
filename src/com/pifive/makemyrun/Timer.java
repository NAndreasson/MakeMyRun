package com.pifive.makemyrun;

import android.os.Handler;
import android.widget.TextView;

public class Timer {
	
	private Handler handler = new Handler();
	private long startTime;
	private int timeElapsed;
	
	public Timer(final TextView clockText) {
		startTime = System.currentTimeMillis();
		Runnable run = new Runnable() {

			@Override
			public void run() {
				
				timeElapsed= (int) (((System.currentTimeMillis() - startTime)/1000) + 0.5);
				int sec = timeElapsed % 60;
				int min = timeElapsed / 60;
				
				clockText.setText("Time ran:\n" + (min < 10 ? "0" + min : min)
						+ ":" + (sec < 10 ? "0" + sec : sec));

				handler.postDelayed(this, 100);
			}
		};
		run.run();
	}
	
	/**
	 * @return time in seconds
	 */
	public int getTime() {
		return timeElapsed;
	}
}
