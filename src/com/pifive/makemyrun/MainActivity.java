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

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	
	private MapView mapView;
	LoadingStatus ls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        Button button = (Button) findViewById(R.id.generatebutton);
        button.setOnClickListener(new OnClickListener() {
			
        	public void onClick(View v) {
        		        		
        		ls = new LoadingStatus(mapView.getContext());
        		try {
        			android.location.Location location = RouteGenerator.getCurrentLocation(getBaseContext());
        			System.out.println(location.toString());
        			String query = RouteGenerator.generateRoute(new com.pifive.makemyrun.Location(location.getLatitude(), location.getLongitude()));
        	        System.out.println(query);
        			startDirectionsTask(query);
        		} catch (NoLocationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
				View overlay = findViewById(R.id.overlayMenu);
				overlay.setVisibility(View.GONE);
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
        DirectionsTask directionsTask = new DirectionsTask(this, DirectionsTask.GOOGLE_URL, ls);
        JSONObject googleRoute = directionsTask.simpleGet(query);
        
        try {
			Route route = new Route(googleRoute);
			RouteDrawer drawer = new RouteDrawer(mapView, route.getWaypoints());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.d("MMR", "ALL DONE");

	}
}
