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

package com.pifive.makemyrun.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * 	A simple class for drawing current location on a MapOverlay.
 * Takes all points it gets from device GPS and updates displayed position with it.
 */
public class CurrentLocationArtist extends AbstractOverlayArtist implements
		LocationListener {

	private Location myLocation;
	private GeoPoint myGeoPoint;
	private float locationSize = 5.0f;

	/**
	 * Sets current location to a best guess provided by creator.
	 * @param initialGuess Our best guess at current location atm.
	 */
	public CurrentLocationArtist(Location initialGuess) {
		myLocation = new Location(initialGuess);
		myGeoPoint = toGeoPoint(myLocation);
	}

	/**
	 * A dark grey 80% opaque paint.
	 */
	@Override
	protected void setupMapPaint() {
		super.setupMapPaint();
		paint.setColor(Color.DKGRAY);
		paint.setAlpha(220);
		paint.setStyle(Paint.Style.FILL);
	}

	/**
	 * Converts an (Android) Location to a Geographical point (GeoPoint).
	 * @param location The location to convert to GeoPoint.
	 * @return Returns the same geographical position provided as a GeoPoint.
	 */
	private GeoPoint toGeoPoint(Location location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
	}
	
	/**
	 * Draws our current position on the MapOverlay(s) we're attached to.
	 * @param canvas
	 * @param mapView
	 * @param shadow
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			Point point = new Point();
			mapView.getProjection().toPixels(myGeoPoint, point);
			canvas.drawCircle(point.x, point.y, locationSize, paint);
		}
	}

	/**
	 * Updates our stored location with the new location from LocationManager
	 * @param location The new location we assume is most correct.
	 */
	@Override
	public void onLocationChanged(Location location) {
		myLocation = location;

		// Update the actual location we're drawing each frame
		myGeoPoint = toGeoPoint(myLocation);
	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Print GPS lost message, ghost the dot

	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Start drawing normally again

	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 * @param status
	 * @param extras
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Maybe we somehow display on the dot which provider we have?

	}

}
