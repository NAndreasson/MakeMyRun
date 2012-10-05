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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.drawing.CurrentLocationArtist;
import com.pifive.makemyrun.drawing.MapDrawer;
import com.pifive.makemyrun.drawing.RouteArtist;

public class MainActivity extends MapActivity implements Observer {
	
	private MapView mapView;
	private MapDrawer mapDrawer;
	private DistanceTracker distanceTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view correctly
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        // Enable map-drawing
        mapDrawer = new MapDrawer(mapView);
        // Listen for generate-click
        Button button = (Button) findViewById(R.id.generatebutton);
        button.setOnClickListener(new OnClickListener() {
			
        	public void onClick(View v) {
        		
        		try {
        			android.location.Location location = RouteGenerator.getCurrentLocation(getBaseContext());
        			String query = RouteGenerator.generateRoute(new com.pifive.makemyrun.Location(location.getLatitude(), location.getLongitude()));
        			startDirectionsTask(query);
        			displayCurrentLocation();
  
        			// Test distanceTracker, should be started when you press run
        			// but that's for the future
        	        trackDistance();
        	        distanceTracker.addObserver(MainActivity.this);
        		} catch (NoLocationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
				View overlay = findViewById(R.id.overlayMenu);
				overlay.setVisibility(View.GONE);

				View distance = findViewById(R.id.distance);
				distance.setVisibility(View.VISIBLE);
				mapView.requestFocus();
				mapView.requestFocusFromTouch();
				mapView.setClickable(true);
			}
        	
        });
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
        JSONObject googleRoute = directionsTask.simpleGet(query);
        
        try {
			Route route = new Route(googleRoute);

			// Center on our starting point
			Location location = route.getWaypoints().get(0);
			GeoPoint geoPoint = new GeoPoint(
									location.getMicroLat(),
									location.getMicroLng());
			mapView.getController().animateTo(geoPoint);
			
			// Add an artist to draw our route
			RouteArtist routeArtist = new RouteArtist(route.getWaypoints());
			mapDrawer.addArtist(routeArtist);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
	
		// Fetch last known location to provide as best guess
		LocationProvider provider = locManager.getProvider(LocationManager.GPS_PROVIDER);
		android.location.Location bestGuess = 
					locManager.getLastKnownLocation(provider.getName());
		
		// Construct our location artist
		CurrentLocationArtist locationArtist = 
					new CurrentLocationArtist(bestGuess, mapDrawer);
		mapDrawer.addArtist(locationArtist);
		
		// Make it aware of location updates every seconds
		locManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				0, 
				0, 
				locationArtist);
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
		TextView distance = (TextView) findViewById(R.id.distance);
		distance.setText("Distance: " + Math.round(distanceTracker.getTotalDistance()) + " m");
	}
}
