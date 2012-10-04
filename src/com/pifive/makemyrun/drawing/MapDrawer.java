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
