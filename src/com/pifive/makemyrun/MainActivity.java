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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	private MapView mapView;
	private View overlay;
	private ViewStub viewStub;
	private ViewStub runViewStub;
	private Button stopRunButton;
	private boolean inCatchBackState = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        viewStub = (ViewStub) findViewById(R.id.viewStub1);
        runViewStub = (ViewStub) findViewById(R.id.viewStub2);
        overlay = findViewById(R.id.overlayMenu);
        updatePosition();
        showStartScreen();
    }
    
    /**
     * Shows the start screen (entry point for application)
     */
    private void showStartScreen() {
        mapView.setBuiltInZoomControls(true);
        overlay.setVisibility(View.VISIBLE);
        Button button = (Button) findViewById(R.id.generatebutton);
        button.setOnClickListener(new OnClickListener() {
			
        	public void onClick(View v) {
				overlay.setVisibility(View.GONE);
				
        		try {
        			android.location.Location location = RouteGenerator.getCurrentLocation(getBaseContext());
        			String query = RouteGenerator.generateRoute(new com.pifive.makemyrun.Location(location.getLatitude(), location.getLongitude()));
        			startDirectionsTask(query);
        		} catch (NoLocationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		showMiddleScreen();
			}
        	
        });
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
				((Button)arg0).setVisibility(View.GONE);
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
				stepBackwards();
			}
		});
    	
    	mapView.requestFocus();
		mapView.requestFocusFromTouch();
		mapView.setClickable(true);
    }
    
    /**
     * Makes the phone get new GPS position so that we can use it later to generate route from accurate position
     */
    private void updatePosition() {
    	PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 
    						 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        LocationManager locationManager = (LocationManager) getBaseContext().
        							  getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestSingleUpdate(provider, pendingIntent);
    }
    
    /**
     * Catches back button when we want it to just step back in application
     */
    @Override
    public void onBackPressed() {
    	if(inCatchBackState) {
    		stepBackwards();
    	} else {
    		finish();
    	}
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
        JSONObject googleRoute = directionsTask.simpleGet(query);
        
        try {
			Route route = new Route(googleRoute);
			@SuppressWarnings("unused")
			RouteDrawer drawer = new RouteDrawer(mapView, route.getWaypoints());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.d("MMR", "ALL DONE");

	}
}
