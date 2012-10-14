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

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * Class for drawing Position pins on to a MapView, receives new GeoPoints when the map is
 * tapped 
 */
public class PositionPlacerArtist extends AbstractOverlayArtist implements MapTapListener {
	
	public enum PinState { START, END, NONE }
	private PinState pinState;
	
	private Map<PinState, PositionPin> positionPins = 
							new EnumMap<PinState, PositionPin>(PinState.class);
	private Drawer drawer;
	 
	/**
	 * Constructs a PositionPlacerArtist from the params
	 * @param startPin - The start position pin
	 * @param endPin - The end position pin
	 * @param drawer - The Drawer we will force to redraw on changes.
	 */
	public PositionPlacerArtist(PositionPin startPin, PositionPin endPin, Drawer drawer) {
		pinState = PinState.NONE;
		positionPins.put(PinState.START, startPin);
		positionPins.put(PinState.END, endPin);
		this.drawer = drawer; 
	}
	
	@Override
	/**
	 * Draw the start and end pin on to the MapOverlay we are attached to
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// iterate over map and draw
		for (Map.Entry<PinState, PositionPin> entry : positionPins.entrySet()) {
			PositionPin positionPin = entry.getValue();
			
			Point point = new Point();
			mapView.getProjection().toPixels(positionPin.getGeoPoint(), point);
			Bitmap bitmap = positionPin.getImage();
			// make the bitmap centered over the position
			canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2, 
					point.y - bitmap.getHeight() / 2, null);
		}		
	}
	
	/**
	 * Returns the start point
	 * @return Returns the start point that has been set
	 */
	public GeoPoint getStartPoint() {
		return positionPins.get(PinState.START).getGeoPoint();
	}
	
	/**
	 * Returns the end point
	 * @return Returns the end point that has been set
	 */
	public GeoPoint getEndPoint() {
		return positionPins.get(PinState.END).getGeoPoint();
	}
	
	/**
	 * Set the pin state (which pin that should be moved on tap)
	 * @param pinState - The pinstate
	 */
	public void setPinState(PinState pinState) {
		this.pinState = pinState; 
	}

	/**
	 * The start or end PositionPins position gets updated depending on the PinState
	 * {@inheritDoc}
	 */
	@Override
	public void onTap(GeoPoint geoPoint, MapView mapView) {
		if (pinState == PinState.START || pinState == PinState.END) {
			positionPins.get(pinState).setGeoPoint(geoPoint);
		}
		drawer.reDraw();
	}
}
