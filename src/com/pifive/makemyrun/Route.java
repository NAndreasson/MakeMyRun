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
	 * 
	 * @param directions A json respond from google. with the structure:
	 * 					route->legs[]->steps[]->polyline->points
	 * @throws JSONException
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
	
	public int getDistance(){
		return distance;
	}	
	
	public List<Location> getWaypoints() {
		return waypoints;
	}
}
