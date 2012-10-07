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

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.geo.Location;

/**
 * An OverlayArtist that draws a route (Android Path) on a MapOverlay.
 * Calculates the Path to draw from a provided list of GeoPoints.
 */
public class RouteArtist extends AbstractOverlayArtist {

	private List<GeoPoint> geoList = new LinkedList<GeoPoint>();
	private Path path = new Path();

	/**
	 * Constructs a RouteArtist to  draw a route on a MapOverlay
	 * @param list A list with geologic locations to construct a Path from.
	 */
	public RouteArtist(List<Location> list) {
		if (list.isEmpty()) {
			throw new EmptyRouteException();
		}
		// Create a list to draw from
		for (Location p : list) {
			geoList.add(new GeoPoint(p.getMicroLat(), p.getMicroLng()));
		}
		Log.d("MMR", "Amount of GeoPoints to draw per frame: " + geoList.size());
	}
	
	/**
	 * Returns a copy of the path being drawn by this artist.
	 * @return Returns the Path being drawn from current GeoPoints.
	 */
	public Path getPath() {
		return new Path(path);
	}

	/**
	 * Draws a path calculated from our GeoPoints on the MapOverlay(s) we're attached to.
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {

			// Construct a path and set its starting location
			path.rewind();
			Point firstPoint = new Point();
			mapView.getProjection().toPixels(geoList.get(0), firstPoint);
			path.moveTo(firstPoint.x, firstPoint.y);

			// Draw lines between all points 
			for (GeoPoint geoPoint : geoList) {
					Point point = new Point();

					mapView.getProjection().toPixels(geoPoint, point);

					path.lineTo(point.x, point.y);
			}
			canvas.drawPath(path, paint);				
		}
	}
}