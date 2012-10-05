package com.pifive.makemyrun;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingStatus {

	private ProgressDialog progBar;

	protected LoadingStatus(Context context) {
		progBar = ProgressDialog.show(context, "Generating Run!", "", true);
	}

	/**
	 * Sets the ProgressDialog message to a certain String.
	 * 
	 * @param s
	 *            The String to be displayed.
	 */
	public void setMessage(String s) {
		progBar.setMessage(s);
	}

	/**
	 * Dismisses the ProgressDialog.
	 */
	public void loadingDone() {
		progBar.dismiss();
	}
}
