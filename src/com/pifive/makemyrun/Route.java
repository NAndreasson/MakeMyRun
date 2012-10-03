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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Route {

	private List<Location> waypoints = new ArrayList<Location>();
	private int distance ;
	
	/**
	 * Creates a route from a JSONObject handed out from Google Directions
	 * @param directions A JSON respond from google. with the structure:
	 * 					route->legs[]->steps[]->polyline->points
	 * @throws JSONException if the strucutre of the JSON is unexpected
	 */
	public Route(JSONObject directions) throws JSONException {
		JSONArray routes = directions.getJSONArray("routes");
		JSONObject route = routes.getJSONObject(0);
		JSONArray legs = route.getJSONArray("legs");
		for(int i = 0 ; i < legs.length(); i ++) {
			JSONObject leg = legs.getJSONObject(i);
			distance += leg.getJSONObject("distance").getInt("value");
			JSONArray steps = getLegSteps(leg);
			for(int j = 0; j < steps.length(); j++){
				String points = steps.getJSONObject(j).
						getJSONObject("polyline").getString("points");
				waypoints.addAll(PolylineDecoder.decodePoly(points));
			}
		}
	}
	
	private JSONArray getLegSteps(JSONObject leg) throws JSONException {
		JSONArray stepsArray = leg.getJSONArray("steps");
		return stepsArray;
	}
	
	/**
	 * 
	 * @return The route's distance 
	 */
	public int getDistance(){
		return distance;
	}	
	
	/**
	 * 
	 * @return The route's waypoint Locations
	 */
	public List<Location> getWaypoints() {
		return waypoints;
	}
}
