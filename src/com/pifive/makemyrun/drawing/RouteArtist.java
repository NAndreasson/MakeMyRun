package com.pifive.makemyrun.drawing;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.Location;

/**
 * An OverlayArtist that draws a route (Android Path) on a MapOverlay.
 * Calculates the Path to draw from a provided list of GeoPoints.
 */
public class RouteArtist extends AbstractOverlayArtist {

	private List<GeoPoint> geoList = new LinkedList<GeoPoint>();
	private List<Path> paths = new LinkedList<Path>();

	/**
	 * Constructs a RouteArtist to  draw a route on a MapOverlay
	 * @param list A list with geologic locations to construct a Path from.
	 */
	public RouteArtist(List<Location> list) {
		
		// Create a list to draw from
		for (Location p : list) {
			geoList.add(new GeoPoint(p.getMicroLat(), p.getMicroLng()));
		}
		Log.d("MMR", "Amount of GeoPoints to draw per frame: " + geoList.size());
	}

	/**
	 * Draws a path calculated from our GeoPoints on the MapOverlay(s) we're attached to.
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {

			// Construct a path and set its starting location
			Path path = new Path();
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