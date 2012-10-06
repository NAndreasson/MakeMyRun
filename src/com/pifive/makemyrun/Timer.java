package com.pifive.makemyrun;

import android.os.Handler;
import android.widget.TextView;

public class Timer {

	private long startTime;
	private int timeElapsed;

	public Timer(final TextView clockText) {
		startTime = System.currentTimeMillis();
		new Thread(new Runnable() {

			private Handler handler = new Handler();

			@Override
			public void run() {

				timeElapsed = (int) (((System.currentTimeMillis() - startTime) / 1000) + 0.5);
				final int sec = timeElapsed % 60;
				final int min = timeElapsed / 60;
				final int hour = timeElapsed / 3600;
				clockText.post(new Runnable() {
					public void run() {
						clockText.setText("Time ran:\n"
								+ (hour < 10 ? "0" + hour : hour) + ":"
								+ (min < 10 ? "0" + min : min) + ":"
								+ (sec < 10 ? "0" + sec : sec));

					}
				});

				handler.postDelayed(this, 100); // ask current thread to rerun
												// after 100 ms
			}
		}).start();
	}

	/**
	 * @return time in seconds
	 */
	public int getTime() {
		return timeElapsed;
	}
}
