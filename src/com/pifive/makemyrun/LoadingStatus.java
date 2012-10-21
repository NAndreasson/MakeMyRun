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

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Handles a ProgressDialog object. For each task running, addItem() should be
 * called.
 * 
 */
public class LoadingStatus {

	private ProgressDialog progress;
	private int tasksInProgress;
	private String message = "";
	
	/**
	 * creates a new LoadingStatus displaying a ProgresDialog.
	 * @param context
	 */
	public LoadingStatus(Context context) {
		progress = ProgressDialog.show(context, "Generating Run!", message, true);
	}

	/**
	 * Adds item for a LoadingStatus to wait for
	 */
	public void addItem() {
		tasksInProgress++;

	}
	/**
	 * 
	 * @return Message displayed by the ProgressDialog
	 */
	public String getMessage(){
		return message;
	}

	/**
	 * dismisses the ProgressDialog
	 */
	public void remove(){
		
		progress.dismiss();				

	}
	/**
	 * Sets loading stage. When all tasks are finished, dismiss the
	 * progressDialog.
	 * 
	 * @param message
	 *            Message to display
	 * @param taskFinished
	 *            Is task finished?
	 */
	public void setLoadingStage(String message, boolean taskFinished) {
		this.message = message+" - "+tasksInProgress+" left";
		progress.setMessage(this.message);
		if (taskFinished && --tasksInProgress <= 0) {
			remove();
		}
	}
}
