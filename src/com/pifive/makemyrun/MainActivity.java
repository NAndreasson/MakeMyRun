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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.database.MMRDbAdapter;
import com.pifive.makemyrun.drawing.CurrentLocationArtist;
import com.pifive.makemyrun.drawing.MapDrawer;
import com.pifive.makemyrun.drawing.RouteArtist;

public class MainActivity extends MapActivity implements Observer {
	private MapView mapView;
	private ViewStub postGeneratedStub;
	private ViewStub runViewStub;
	private ViewStub mainMenuStub;
	private boolean inCatchBackState = false;
	private MapDrawer mapDrawer;
	private DistanceTracker distanceTracker;
    private Timer timer;
	private LoadingStatus loadingStatus;
	private MMRDbAdapter db;
	private Route currentRoute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view correctly
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        postGeneratedStub = (ViewStub) findViewById(R.id.postGeneratedStub);
        runViewStub = (ViewStub) findViewById(R.id.runningInterface);
        mainMenuStub = (ViewStub) findViewById(R.id.mainMenuStub);
        mapDrawer = new MapDrawer(mapView);
        db = new MMRDbAdapter(getBaseContext());
        showStartScreen();
        displayCurrentLocation();
    }
    
    /**
     * Shows the start screen (entry point for application)
     */
    private void showStartScreen() {
        mainMenuStub.setVisibility(View.VISIBLE);     
    }
    
    private Location getCurrentLocation() {
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	
		// Fetch last known location to provide as best guess
		String provider = locManager.getBestProvider(new Criteria(), true);
		//locManager.getProvider(LocationManager.GPS_PROVIDER);
		Log.d("MMR", "im using provider : "+ provider);
		android.location.Location currentLocation = 
					locManager.getLastKnownLocation(provider);
    	
		if (currentLocation == null) {
			throw new NoLocationException("Location unavailable");
		}
		return currentLocation;
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
    	
    	// Start to track the distance and sets this activity to recieve distance updates
    	trackDistance();
        distanceTracker.addObserver(MainActivity.this);

        // Start counting the clockwatch for this run
        timer = new Timer((TextView) findViewById(R.id.clocktext));
    	timer.start();
    	
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
    
    public void generateRoute(View v) {
    	mainMenuStub.setVisibility(View.GONE);
		loadingStatus = new LoadingStatus(mapView.getContext());
		try {
			// send the current location to routegenerator
			Location currentLocation = getCurrentLocation();
			String query = RouteGenerator.generateRoute(
							new com.pifive.makemyrun.geo.Location(currentLocation.getLatitude(), currentLocation.getLongitude()));
			startDirectionsTask(query);
		} catch (RuntimeException e) {
			loadingStatus.remove();
			Toast.makeText(getApplicationContext(), "ERROR: "+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return;
		}
		
		showMiddleScreen();
		
//		View overlay = findViewById(R.id.overlayMenu);
//		overlay.setVisibility(View.GONE);
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
	 * Queries Google Directions with supplied query through a task.
	 * @param query The query to execute DirectionsTask with.
	 */
	private void startDirectionsTask(String query) {
        DirectionsTask directionsTask = new DirectionsTask(this, DirectionsTask.GOOGLE_URL);
        directionsTask.setLoadingStatus(loadingStatus);
        JSONObject googleRoute = directionsTask.simpleGet(query);
        
        try {
			currentRoute = new Route(googleRoute);

			// Center on our starting point
			com.pifive.makemyrun.geo.Location location = currentRoute.getWaypoints().get(0);
			GeoPoint geoPoint = new GeoPoint(
									location.getMicroLat(),
									location.getMicroLng());
			mapView.getController().animateTo(geoPoint);
			
			// Add an artist to draw our route
			RouteArtist routeArtist = new RouteArtist(currentRoute.getWaypoints());
			mapDrawer.addArtist(routeArtist);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructs a MyLocationDrawer to draw current location.
	 */
	private void displayCurrentLocation() {
		
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Construct our location artist
		CurrentLocationArtist locationArtist = new CurrentLocationArtist(mapDrawer);
		mapDrawer.addArtist(locationArtist);
		
		// Make it aware of location updates from all providers
		List<String> providers = locManager.getAllProviders();
		for (String provider : providers) {
			locManager.requestLocationUpdates(
					provider,
					0, 
					0, 
					locationArtist);
		}
	}
	
	/**
	 * Constructs a DistanceTracker to track distance
	 */
	private void trackDistance() {
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	
		// Get the starting position
		String provider = LocationManager.GPS_PROVIDER;
		android.location.Location currentLocation = 
					locManager.getLastKnownLocation(provider);
			
		distanceTracker = new DistanceTracker(currentLocation);
		// set distanceTracker to retrieve location updates
		locManager.requestLocationUpdates(provider, 0, 0, distanceTracker);
	}

	@Override
	public void update(Observable observable, Object data) {
		TextView distance = (TextView) findViewById(R.id.distancetext);
		distance.setText(Math.round(distanceTracker.getTotalDistanceInMeters()) + " m");
	}
	
	public void viewHistory(View v) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}
	
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
				Toast.makeText(getBaseContext(), saveRun(completed) ?    // look close, aculy we save hear
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
	
	public boolean saveRun(boolean completed) {
		Log.d("MMR", "Route distance: "+currentRoute.getDistance());
		Log.d("MMR", "Ran distance: " + distanceTracker.getTotalDistanceInMeters());
		db.open();
		boolean result = db.createRun(
				currentRoute.getPolyline(),
				timer.getStartTime(), 
				(int) distanceTracker.getTotalDistanceInMeters(), 
				currentRoute.getDistance(), 
				completed) != -1;

		db.close();
		return result; 
	}
	
	public void cleanUp() {
    	mapDrawer.clearDrawer();
    	mapView.setClickable(false);
    	mapView.invalidate();
    	if (timer instanceof Timer) {
    		timer.stop();    		
    	}
	}
}
