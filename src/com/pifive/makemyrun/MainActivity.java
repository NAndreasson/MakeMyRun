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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.drawing.CurrentLocationArtist;
import com.pifive.makemyrun.drawing.MapDrawer;
import com.pifive.makemyrun.drawing.PositionPin;
import com.pifive.makemyrun.drawing.PositionPlacerArtist;
import com.pifive.makemyrun.drawing.PositionPlacerArtist.PinState;
import com.pifive.makemyrun.drawing.RouteArtist;

public class MainActivity extends MapActivity implements Observer {
	private MapView mapView;
	private ViewStub viewStub;
	private ViewStub runViewStub;
	private ViewStub mainMenuStub;
	private Button stopRunButton;
	private boolean inCatchBackState = false;
	private MapDrawer mapDrawer;
	private DistanceTracker distanceTracker;
    private Timer timer;
	private LoadingStatus loadingStatus;
	
	private GeoPoint startPoint; 
	private GeoPoint endPoint;
	private ViewStub generateRouteStub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view correctly
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        viewStub = (ViewStub) findViewById(R.id.postGeneratedStub);
        runViewStub = (ViewStub) findViewById(R.id.runningInterface);
        generateRouteStub = (ViewStub) findViewById(R.id.generateRouteStub);
        mainMenuStub = (ViewStub) findViewById(R.id.mainMenuStub);
        mapDrawer = new MapDrawer(mapView);
        
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
		android.location.Location currentLocation = 
					locManager.getLastKnownLocation(provider);
    	
		if (currentLocation == null) {
			throw new NoLocationException("Location unavailable");
		}
		return currentLocation;
    }
    
    /**
     * Shows the middle screen and activates the state where back button takes you backwards
     */
    private void showMiddleScreen() {
    	inCatchBackState = true;
        viewStub.setVisibility(View.VISIBLE);
        Button runButton = (Button) findViewById(R.id.runbutton);
        
        runButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startRun();
			}
        });
    }
    
    /**
     * Starting the run
     */
    private void startRun() {
    	viewStub.setVisibility(View.GONE);
    	runViewStub.setVisibility(View.VISIBLE);

        stopRunButton = (Button) findViewById(R.id.stoprunbutton);
    	stopRunButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				cleanUpRun();
				stepBackwards();
			}
		});
    	
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
     * To be called when a generated route is to be disposed of.
     */
    private void cleanUpRun() {
    	mapDrawer.clearDrawer();
    	mapView.invalidate();
    	if (timer instanceof Timer) {
    		timer.stop();    		
    	}
    }
    
    
    /**
     * Catches back button when we want it to just step back in application
     */
    @Override
    public void onBackPressed() {
    	if(inCatchBackState) {
    		cleanUpRun();
    		stepBackwards();
    	} else {
    		finish();
    	}
    }
    
    public void chooseStartEndPoints(View v) {    	
    	mainMenuStub.setVisibility(View.GONE);
    	generateRouteStub.setVisibility(View.VISIBLE);
    	Location currentLocation = getCurrentLocation(); 
    	Bitmap pinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
    	PositionPin startPin = new PositionPin(toGeoPoint(currentLocation), pinBitmap);
    	PositionPin endPin = new PositionPin(toGeoPoint(currentLocation), pinBitmap);
    	final PositionPlacerArtist positionPlacerArtist = 
    			new PositionPlacerArtist(startPin, endPin, mapDrawer);
    	
    	mapDrawer.addArtist(positionPlacerArtist);
    	mapView.setClickable(true);
    	Button startPointButton = (Button) findViewById(R.id.startpointbutton);
    	startPointButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				positionPlacerArtist.setPinState(PinState.START);
			}
		});

    	Button endPointButton = (Button) findViewById(R.id.endpointbutton);
    	endPointButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				positionPlacerArtist.setPinState(PinState.END);
			}
		});

    	Button generateButton = (Button) findViewById(R.id.generateRouteButton);
    	generateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO naming conventions, destination or end
				startPoint = positionPlacerArtist.getStartPoint();
				endPoint = positionPlacerArtist.getEndPoint();
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
		loadingStatus = new LoadingStatus(mapView.getContext());
		try {
			String query = RouteGenerator.generateRoute(
							startPoint, endPoint);
			startDirectionsTask(query);
			
		} catch (RuntimeException e) {
			loadingStatus.remove();
			Toast.makeText(getApplicationContext(), "ERROR: "+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return;
		}
		
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
    	viewStub.setVisibility(View.GONE);
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
			Route route = new Route(googleRoute);

			// Center on our starting point
			com.pifive.makemyrun.geo.Location location = route.getWaypoints().get(0);
			GeoPoint geoPoint = new GeoPoint(
									location.getMicroLat(),
									location.getMicroLng());
			mapView.getController().animateTo(geoPoint);
			
			// Add an artist to draw our route
			RouteArtist routeArtist = new RouteArtist(route.getWaypoints());
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
		Log.d("MMR", "Does this run? ");
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
}
