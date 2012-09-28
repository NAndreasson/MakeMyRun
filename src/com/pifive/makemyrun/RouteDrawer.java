package com.pifive.makemyrun;

import java.util.Iterator;
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

	public RouteDrawer(MapView mapView, List<Location> list) {
		Log.d("MMR", "Consteu");
		mapView.getOverlays().add(new RouteOverlay(list, mapView.getProjection()));
		
	}
	
	private class RouteOverlay extends Overlay {
		
		private final List<Location> pointList;
		private final Projection projection;
		
		public RouteOverlay(List<Location> pointList, Projection projection) {
			
			this.projection = projection;
			this.pointList = pointList;
		}
		
		@Override
		public void draw(Canvas canvas, MapView mv, boolean shadow) {
			super.draw(canvas, mv, shadow);
			Log.d("MMR", "DRAW method");
			Paint mPaint = new Paint();
			mPaint.setDither(shadow);
			mPaint.setColor(Color.BLUE);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(2);
			
			GeoPoint startPoint = null;
			GeoPoint endPoint = null;
			
			for (Location point : pointList) {
				endPoint = new GeoPoint(point.getLatInt(), point.getLngInt());
				
				
				if (startPoint instanceof GeoPoint && endPoint instanceof GeoPoint) {
					// HERE DRAW SOME SHIT
					Point p1 = new Point();
					Point p2 = new Point();
					
					Path path = new Path();
					mv.getController().animateTo(endPoint);
					
					projection.toPixels(startPoint, p1);
					projection.toPixels(endPoint, p2);
					
					path.moveTo(p2.x, p2.y);
					path.lineTo(p1.x, p1.y);
					Log.d("MMR", p1 + " & " + p2);
					canvas.drawPath(path, mPaint);
				}
				startPoint = endPoint;
			}
			
		}
		
	}

}
