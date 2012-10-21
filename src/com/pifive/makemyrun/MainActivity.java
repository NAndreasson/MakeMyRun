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

import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.drawing.PositionPin;
import com.pifive.makemyrun.drawing.PositionPlacerArtist;
import com.pifive.makemyrun.drawing.PositionPlacerArtist.PinState;

public class MainActivity extends MapActivity implements Observer {
	private MapView mapView;
	private ViewStub postGeneratedStub;
	private ViewStub runViewStub;
	private ViewStub mainMenuStub;
	private boolean inCatchBackState = false;
	private ViewStub generateRouteStub;
	private RunController runController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        postGeneratedStub = (ViewStub) findViewById(R.id.postGeneratedStub);
        runViewStub = (ViewStub) findViewById(R.id.runningInterface);
        generateRouteStub = (ViewStub) findViewById(R.id.generateRouteStub);
        mainMenuStub = (ViewStub) findViewById(R.id.mainMenuStub);
        runController = new RunController(this, mapView);

        runController.displayCurrentLocation();
        showStartScreen();
    }
    
    /**
     * Shows the start screen (entry point for application)
     */
    private void showStartScreen() {
        mainMenuStub.setVisibility(View.VISIBLE);     
    }
    
    /**
     * Shows the middle screen 
     */
    private void showMiddleScreen() {
    	inCatchBackState = true;
        postGeneratedStub.setVisibility(View.VISIBLE);
    }
    
    /**
     * Starting the run
     */
    public void startRunAction(View v) {
    	postGeneratedStub.setVisibility(View.GONE);
    	runViewStub.setVisibility(View.VISIBLE);
    	
    	runController.startRunLogic(this);
        mapView.requestFocus();
		mapView.requestFocusFromTouch();
		mapView.setClickable(true);
    }
    
    /**
     * Catches back button when we want it to just step back in application
     */
    @Override
    public void onBackPressed() {
    	if(inCatchBackState) {
    		cleanUp();
    		stepBackwards();
    	} else {
    		finish();
    	}
    }
    
    public void chooseStartEndPoints(View v) {    	
    	mainMenuStub.setVisibility(View.GONE);
    	generateRouteStub.setVisibility(View.VISIBLE);
    	
    	// the default location should be the current one
    	Location currentLocation = runController.getCurrentLocation(); 
    	
    	Bitmap positionPinImage = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
    	PositionPin startPin = new PositionPin(toGeoPoint(currentLocation), positionPinImage);
    	PositionPin endPin = new PositionPin(toGeoPoint(currentLocation), positionPinImage);
    	
    	final PositionPlacerArtist positionPlacerArtist = 
    			new PositionPlacerArtist(startPin, endPin, runController.getMapDrawer());
    	runController.getMapDrawer().addArtist(positionPlacerArtist);
    	mapView.setClickable(true);
    	
    	Button startPointButton = (Button) findViewById(R.id.startpointbutton);
    	startPointButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				positionPlacerArtist.setPinState(PinState.START);
			}
		});

    	Button endPointButton = (Button) findViewById(R.id.endpointbutton);
    	endPointButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				positionPlacerArtist.setPinState(PinState.END);
			}
		});

    	Button generateButton = (Button) findViewById(R.id.generateRouteButton);
    	generateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.this.runController.setStartPoint(positionPlacerArtist.getStartPoint());
				MainActivity.this.runController.setEndPoint(positionPlacerArtist.getEndPoint());
				generateRouteStub.setVisibility(View.GONE);
				positionPlacerArtist.setPinState(PinState.NONE);
				generateRoute(v);
			}
		});
    }

	/**
	 * Converts an (Android) Location to a Geographical point (GeoPoint).
	 * @param location The location to convert to GeoPoint.
	 * @return Returns the same geographical position provided as a GeoPoint.
	 */
    /*
     * TODO same method exists inside the RouteArtist class, should GeoPoints be sent to it
     * instead of locations, or the other way around? 
     */
	public static GeoPoint toGeoPoint(Location location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
	}
	
    public void generateRoute(View v) {
    	runController.generateRoute(mapView);
		showMiddleScreen();
		mapView.requestFocus();
		mapView.requestFocusFromTouch();
		mapView.setClickable(true);
		
    }

    /**
     * Process for stepping backwards and resetting application state.
     */
    private void stepBackwards() {
    	inCatchBackState = false;
    	postGeneratedStub.setVisibility(View.GONE);
    	runViewStub.setVisibility(View.GONE);
   		mapView.getOverlays().clear();
   		mapView.setClickable(false);
   		mapView.clearFocus();
		showStartScreen();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Updates distance text
	 */
	@Override
	public void update(Observable observable, Object data) {
		TextView distance = (TextView) findViewById(R.id.distancetext);
		distance.setText(Math.round(runController.getDistanceTracker().getTotalDistanceInMeters()) + " m");
	}
	
	/**
	 * Starts history activity
	 * @param v
	 */
	public void viewHistory(View v) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 
	 * @param v
	 */
	public void onStopAction(View v) {
		final boolean completed = Boolean.valueOf(""+v.getTag());
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.stop_run_alert_dialog_message)
		       .setTitle(R.string.stop_run_alert_dialog_title);
		
		final ViewStub runStub = runViewStub;
		final ViewStub mainMenuStub = this.mainMenuStub;

		builder.setPositiveButton(R.string.stop_run_alert_dialog_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				runStub.setVisibility(View.GONE);
				mainMenuStub.setVisibility(View.VISIBLE);
				Toast.makeText(getBaseContext(), runController.saveRun(completed) ?
						R.string.save_run_success :
						R.string.save_run_failed, Toast.LENGTH_LONG).show();	
				cleanUp();

			}
			
		});
		builder.setNegativeButton(R.string.stop_run_alert_dialog_no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return; // nothing to do here :D
			}
		});
		
		builder.create().show();
	}
	
	/**
	 * Cleans up from a run
	 */
	public void cleanUp() {
    	runController.cleanUp(); 
    	mapView.setClickable(false);
    	mapView.invalidate();	
	}
}
