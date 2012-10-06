package com.pifive.makemyrun;

import java.util.Observable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Class for tracking distance, should listen for updates 
 * from LocationManager
 */
public class DistanceTracker extends Observable implements LocationListener {
	
	private Location lastPoint;
	private float totalDistance = 0;
	
	/**
	 * Constructs the Distance tracker, starting to track distance from the starting location
	 * @param startingPoint -  The location from which to start tracking distance
	 */
	public DistanceTracker(Location startingLocation) {
		lastPoint = startingLocation;
	}
	
	/**
	 * Returns the total distance moved
	 * @return the distance moved
	 */
	public float getTotalDistance() {
		return totalDistance;
	}
	
	/**
	 * Updates distance moved with the help of the new location. If the accuracy is good enough.
	 */
	@Override
	public void onLocationChanged(Location location) {
		float distanceBetween = lastPoint.distanceTo(location);
		// so distance does not increase when standing still
		if ( distanceBetween > location.getAccuracy() ) {
			totalDistance += distanceBetween;
			lastPoint = location;

			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Unused
	 */
	@Override
	public void onProviderDisabled(String provider) {
		
	}

	/**
	 * Unused
	 */
	@Override
	public void onProviderEnabled(String provider) {
		
	}

	/**
	 * Unused
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

}
