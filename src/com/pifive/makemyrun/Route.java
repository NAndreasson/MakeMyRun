package com.pifive.makemyrun;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Route {

	private List<Location> waypoints = new ArrayList<Location>();
	private int distance ;
	
	public Route(List<Location> waypoints){
		this.waypoints = waypoints;
	}
	
	public Route(JSONObject directions) throws JSONException {
		JSONObject route = directions.getJSONArray("routes").getJSONObject(0);
		JSONArray legs = route.getJSONArray("legs");
		for(int i = 0 ; i < legs.length(); i ++) {
			JSONObject leg = legs.getJSONObject(i);
			distance += leg.getJSONObject("distance").getInt("value");
			JSONArray steps = getLegSteps(leg);
			addLocations(steps);
		}
		JSONObject lastLeg = legs.getJSONObject(legs.length()-1);
		addLastStepLocation(lastLeg);
	}
	
	private JSONArray getLegSteps(JSONObject leg) throws JSONException {
		JSONArray stepsArray = leg.getJSONArray("steps");
		return stepsArray;
	}
	
	private void addLocations(JSONArray steps) throws JSONException {
		for(int j = 0; j < steps.length() ; j++) { 
			JSONObject location = steps.getJSONObject(j).getJSONObject("start_location");
			waypoints.add(new Location(location.getDouble("lng"), location.getDouble("lat")));
		}
	}
	
	public void addLastStepLocation(JSONObject lastLeg) throws JSONException {
		JSONArray lastLegSteps = lastLeg.getJSONArray("steps");
		JSONObject lastLegStep = lastLegSteps.getJSONObject(lastLegSteps.length()-1);
		JSONObject endLocation = lastLegStep.getJSONObject("end_location");
		waypoints.add(new Location(endLocation.getDouble("lng"), endLocation.getDouble("lat")));
	}
	
	public int getDistance(){
		return distance;
	}	
	
	public List<Location> getWaypoints() {
		return waypoints;
	}
}
