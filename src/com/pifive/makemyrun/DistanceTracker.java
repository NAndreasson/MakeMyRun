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
	 * Returns the total distance moved in meters
	 * @return the distance moved
	 */
	public float getTotalDistanceInMeters() {
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
