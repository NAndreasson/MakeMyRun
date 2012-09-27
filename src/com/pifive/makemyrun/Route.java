package com.pifive.makemyrun;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;

public class Route {

	private List<Location> waypoints;
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
			
			JSONArray stepsArray = leg.getJSONArray("steps");
			
			for(int j = 0; i < stepsArray.length() ; j++) { 
				JSONObject location = stepsArray.getJSONObject(j).getJSONObject("start_location");
				waypoints.add(new Location(location.getDouble("lng"), location.getDouble("lat")));
			}
		}
		JSONObject endLeg = legs.getJSONObject(legs.length()-1);
		JSONArray endLegSteps = endLeg.getJSONArray("steps");
		JSONObject endLegEndStep = endLegSteps.getJSONObject(endLegSteps.length()-1);
		JSONObject endLocation = endLegEndStep.getJSONObject("end_location");
		waypoints.add(new Location(endLocation.getDouble("lng"), endLocation.getDouble("lat")));
		
		
	}
	
	public int getdistance(){
		return distance;
	}
	
}
