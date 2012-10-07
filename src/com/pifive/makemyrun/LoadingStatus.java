package com.pifive.makemyrun;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

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
	public void cancel(){
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
			progress.dismiss();
		}
	}
}
