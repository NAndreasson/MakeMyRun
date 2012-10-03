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
 * 
 * @author atamon
 *
 */
public class MyLocationDrawer implements LocationListener {
	
	private MapView mapView;
	private Location myLocation;
	private MyLocationOverlay locOverlay;

	/**
	 * 
	 * @param mapView
	 * @param initialGuess
	 */
	public MyLocationDrawer(MapView mapView, Location initialGuess) {
		myLocation = new Location(initialGuess);
		
		locOverlay = new MyLocationOverlay(toGeoPoint(myLocation), mapView.getProjection());
		mapView.getOverlays().add(locOverlay);
	}
	
	/**
	 * 
	 * @param loc
	 * @return
	 */
	private GeoPoint toGeoPoint(Location loc) {
		return new GeoPoint(
				(int) (loc.getLatitude()*1E6), 
				(int) (loc.getLongitude()*1E6));
	}
	
	/**
	 * 
	 * @param location
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		if (myLocation != null && myLocation.getAccuracy() >= location.getAccuracy()) {
			myLocation = location;
			
			// Update the actual location we're drawing each frame
			locOverlay.updateLocation(toGeoPoint(myLocation));
		}
	}

	/**
	 * 
	 * @param provider
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Print GPS lost message, ghost the dot
		
	}

	/**
	 * 
	 * @param provider
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Start drawing normally again
		
	}

	/**
	 * 
	 * @param provider
	 * @param status
	 * @param extras
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Maybe we somehow display on the dot which provider we have?
		
	}
	
	/**
	 * 
	 * @author atamon
	 *
	 */
	private class MyLocationOverlay extends Overlay {
		
		protected GeoPoint myPoint;
		
		private Paint locPaint = new Paint();
		private Projection projection;
		private float pointSize = 5.0f;
		
		/**
		 * 
		 * @param initialPoint
		 * @param projection
		 */
		public MyLocationOverlay(GeoPoint initialPoint, Projection projection) {
			myPoint = initialPoint;
			this.projection = projection;
			
			locPaint.setDither(true);
			locPaint.setColor(Color.DKGRAY);
			locPaint.setAlpha(220);
			locPaint.setStyle(Paint.Style.FILL);
		}
		
		/**
		 * 
		 * @param location
		 */
		protected void updateLocation(GeoPoint location) {
			myPoint = new GeoPoint(
								location.getLatitudeE6(), 
								location.getLongitudeE6());
			Log.d("MMR", "Drawing mylocation at new geographical location: "
						+ myPoint);
		}
		
		/**
		 * 
		 * @param canvas
		 * @param mapView
		 * @param shadow
		 */
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			if (!shadow) {
				Point point = new Point();
				projection.toPixels(myPoint, point);
				canvas.drawCircle(point.x, point.y, pointSize, locPaint);
			}
		}
		
		
	}

}
