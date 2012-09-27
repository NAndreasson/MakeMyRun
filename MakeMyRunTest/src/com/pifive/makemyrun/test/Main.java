package com.pifive.makemyrun.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.pifive.makemyrun.MainActivity;

public class Main extends ActivityInstrumentationTestCase2<MainActivity> {
	
	private Activity myActivity;
	
	public Main() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		
		myActivity = getActivity();
	}
	
}
