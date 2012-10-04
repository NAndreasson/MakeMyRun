package com.pifive.makemyrun.drawing;

import android.graphics.Canvas;

import com.google.android.maps.MapView;

/**
 * An OverlayArtist hooks to a MapDrawer's MapOverlay draw method.
 * So it must implement what to draw by itself, and setup paint.
 */
public interface OverlayArtist {

	public void draw(Canvas canvas, MapView mapView, boolean shadow);
}
