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

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteDrawer {

	MapView mapView;
	List<GeoPoint> geoList = new LinkedList<GeoPoint>();

	/**
	 * Constructs a RouteDrawer from the provided MapView
	 * and list of locations.
	 * @param mapView The MapView we wish to draw the route on
	 * @param list A list with geologic locations.
	 */
	public RouteDrawer(MapView mapView, List<Location> list) {
		
		// Create a list to draw from
		for (Location p : list) {
			geoList.add(new GeoPoint(p.getMicroLat(), p.getMicroLng()));
		}

		// Add overlay to draw route on
		mapView.getOverlays().add(
				new RouteOverlay(geoList, mapView.getProjection()));
		
		// Center on start location.
		Location point = list.get(0);
		mapView.getController().setCenter(new GeoPoint(point.getMicroLat(), point.getMicroLng()));
	}

	/**
	 * An overlay used to draw Google generated Routes on a MapView.
	 */
	private class RouteOverlay extends Overlay {

		private final List<GeoPoint> pointList;
		private final Projection projection;
		private final Paint mPaint = new Paint();

		public RouteOverlay(List<GeoPoint> pointList, Projection projection) {

			this.projection = projection;
			this.pointList = pointList;
			setupMapPaint();
			
			Log.d("MMR", "Amount of GeoPoints to draw per call: " + pointList.size());
		}

		/**
		 * Draws route on MapView from pointList.first to pointList.last
		 * So to draw a closed route one must provide a list with first equals last
		 * {@inheritDoc} 
		 */
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			//We only draw on !shadow so we don't draw twice.
			if (!shadow) {

				GeoPoint startPoint = null;
				GeoPoint endPoint = null;

				for (GeoPoint point : pointList) {
					endPoint = point;

					if (startPoint instanceof GeoPoint
							&& endPoint instanceof GeoPoint) {

						Point p1 = new Point();
						Point p2 = new Point();

						Path path = new Path();
						mapView.getController().animateTo(endPoint);

						projection.toPixels(startPoint, p1);
						projection.toPixels(endPoint, p2);

						path.moveTo(p2.x, p2.y);
						path.lineTo(p1.x, p1.y);
						canvas.drawPath(path, mPaint);
					}
					startPoint = endPoint;
				}
			}

		}

		/**
		 * Configures the paint tool which we will use for drawing the path.
		 */
		public void setupMapPaint() {
			mPaint.setDither(true);
			mPaint.setColor(Color.BLUE);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(5);
		}

	}

}