package com.pifive.makemyrun;

import java.util.Observable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class DistanceTracker extends Observable implements LocationListener {
	
	private Location lastPoint;
	private float totalDistance = 0;
	
	public DistanceTracker(Location startingPoint) {
		lastPoint = startingPoint;
	}
	
	public float getTotalDistance() {
		return totalDistance;
	}
	
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

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

}
