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

package com.pifive.makemyrun;

import android.os.Handler;
import android.widget.TextView;

/**
 * Displays and updates a stopwatch for a running interface for as long as this
 * object exists.
 */
public class Timer {

	private long startTime;
	private int timeElapsed;

	private final Thread timerThread;
	private final TimerRunnable timerRunnable;

	/**
	 * Creates a stopwatch which continuously updates its clockText with current
	 * time since start.
	 * 
	 * @param clockText
	 *            The GUI view to print the text on.
	 */
	public Timer(final TextView clockText) {
		timerRunnable = new TimerRunnable(clockText);
		timerThread = new Thread(timerRunnable);
	}

	/**
	 * Starts the stopwatch
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		timerThread.start();
	}

	/**
	 * Stops the stopwatch
	 */
	public void stop() {
		timerRunnable.running = false;
	}

	public boolean isRunning() {
		return timerRunnable.running;
	}

	/**
	 * 
	 * @return Unix Time in seconds when the timer was started
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * A runnable which can be stopped like a timer.
	 */
	private class TimerRunnable implements Runnable {

		private final TextView clockText;
		private Handler handler = new Handler();
		private boolean running = true;

		protected TimerRunnable(TextView view) {
			this.clockText = view;
		}

		/**
		 * Continuously updates Timer's clockText with time elapsed since start.
		 */
		@Override
		public void run() {

			timeElapsed = (int) (((System.currentTimeMillis() - startTime) / 1000) + 0.5);
			final int sec = timeElapsed % 60;
			final int min = timeElapsed / 60;
			final int hour = timeElapsed / 3600;
			clockText.post(new Runnable() {
				public void run() {
					clockText.setText((hour < 10 ? "0" + hour : hour) + ":"
							+ (min < 10 ? "0" + min : min) + ":"
							+ (sec < 10 ? "0" + sec : sec));
				}
			});

			if (running) {
				// ask current thread to rerun after 100 ms
				handler.postDelayed(this, 100);
			}
		}
	}
}
