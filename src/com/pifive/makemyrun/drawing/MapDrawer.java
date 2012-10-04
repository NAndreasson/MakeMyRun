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

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Adds an overlay to a MapView which allows OverlayArtists to draw on
 * the overlay.
 * To reduce process time we draw on the same overlay.
 */
public class MapDrawer {

	private MapView mapView;
	private MapOverlay overlay;
	
	/**
	 * Constructs a MapDrawer for the provided MapView
	 * @param mapView The mapview to attach an overlay and draw on.
	 */
	public MapDrawer(MapView mapView) {
		this.mapView = mapView;

		overlay = new MapOverlay();
		mapView.getOverlays().add(overlay);
	}

	/**
	 * Add an artist to our overlay
	 * (and if needed attach the overlay again).
	 * @param artist The artist we will allow to draw with us.
	 */
	public void addArtist(OverlayArtist artist) {
		if (!mapView.getOverlays().contains(overlay)) {
			mapView.getOverlays().add(overlay);
		}
		overlay.addArtist(artist);
	}

	/**
	 * Remove an artist from our overlay
	 * (and if overlay is empty remove it).
	 * @param artist The artist to be removed
	 */
	public void removeArtist(OverlayArtist artist) {
		overlay.removeArtist(artist);
		if (overlay.getNumberofArtists() == 0) {
			mapView.getOverlays().remove(overlay);
		}
	}

	/**
	 * An overlay which asks its OverlayArtists what to draw today.
	 */
	private class MapOverlay extends Overlay {

		private List<OverlayArtist> artists = new LinkedList<OverlayArtist>();
		
		/**
		 * Returns the number of artists that are drawing with us.
		 * @return The actual size of an artists list.
		 */
		protected int getNumberofArtists() {
			return artists.size();
		}

		/**
		 * Add an artist to the list of drawers.
		 * @param artist The artist to help us draw.
		 */
		protected void addArtist(OverlayArtist artist) {
			artists.add(artist);
		}

		/**
		 * Removes an artist from our list of drawers.
		 * @param artist The artist we will not allow to draw any longer.
		 */
		protected void removeArtist(OverlayArtist artist) {
			artists.remove(artist);
		}

		/**
		 * Draws the canvas with help from all our artists.
		 */
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			for (OverlayArtist artist : artists) {
				artist.draw(canvas, mapView, shadow);
			}
		}
	}
}
