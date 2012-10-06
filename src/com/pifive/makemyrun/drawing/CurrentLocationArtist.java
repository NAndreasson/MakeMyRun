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

/**
 * 	A simple class for drawing current location on a MapOverlay.
 * Takes all points it gets from device GPS and updates displayed position with it.
 */
public class CurrentLocationArtist extends AbstractOverlayArtist implements
		LocationListener {

	private Drawer drawer;
	
	// Variables to keep track of, trace and draw the actual position 
	private Location myLocation;
	private GeoPoint myGeoPoint;
	private Point point = new Point();
	
	private float locationSize = 0.7f;

	/**
	 * Sets current location to a best guess provided by creator.
	 * @param initialGuess Our best guess at current location atm.
	 * @param drawer The Drawer we will force to redraw on changes.
	 */
	public CurrentLocationArtist(Location initialGuess, Drawer drawer) {
		this.drawer = drawer;
		myLocation = new Location(initialGuess);
		myGeoPoint = toGeoPoint(myLocation);
	}

	/**
	 * Converts an (Android) Location to a Geographical point (GeoPoint).
	 * @param location The location to convert to GeoPoint.
	 * @return Returns the same geographical position provided as a GeoPoint.
	 */
	public static GeoPoint toGeoPoint(Location location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
	}

	/**
	 * A green 80% opaque paint.
	 */
	@Override
	protected void setupMapPaint() {
		super.setupMapPaint();
		paint.setARGB(255, 230, 50, 50);
		paint.setStyle(Paint.Style.FILL);
	}
	
	/**
	 * Returns a copy of the point being drawn atm.
	 * @return Returns a copy of the point being drawn.
	 */
	public Point getPoint() {
		return new Point(point);
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
			mapView.getProjection().toPixels(myGeoPoint, point);
			canvas.drawCircle(point.x, point.y, 
								locationSize*mapView.getZoomLevel(), paint);
		}
	}

	/**
	 * Updates our stored location with the new location from LocationManager
	 * if the new location's accuracy is great enough.
	 * @param location The new location we assume is most correct.
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (myLocation.distanceTo(location) > location.getAccuracy()) {				
			myLocation = location;
	
			// Update the actual location we're drawing each frame
			myGeoPoint = toGeoPoint(myLocation);
			drawer.reDraw();
		}
	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 */
	@Override
	public void onProviderDisabled(String provider) {
		Log.d("MMR", "Warning: GPS provider disabled");
	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 */
	@Override
	public void onProviderEnabled(String provider) {
		Log.d("MMR", "GPS provider enabled! :)");
	}

	/**
	 * UNIMPLEMENTED/UNUSED
	 * @param provider
	 * @param status
	 * @param extras
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Maybe we somehow display on the dot which provider we have in the future?

	}

}
