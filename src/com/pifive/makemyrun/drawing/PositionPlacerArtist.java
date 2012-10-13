package com.pifive.makemyrun.drawing;

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PositionPlacerArtist extends AbstractOverlayArtist implements MapTapListener {
	private Map<PinState, PositionPin> positionPins = 
			new EnumMap<PinState, PositionPin>(PinState.class);
	private MapDrawer drawer;
	
	public enum PinState { START, END, NONE }
	private PinState pinState; 
	 
	public PositionPlacerArtist(PositionPin startPin, PositionPin destinationPin, MapDrawer drawer) {
		pinState = PinState.NONE;
		positionPins.put(PinState.START, startPin);
		positionPins.put(PinState.END, destinationPin);
		this.drawer = drawer; 
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// iterate over map and draw
		for (Map.Entry<PinState, PositionPin> entry : positionPins.entrySet()) {
			PositionPin positionPin = entry.getValue();
			Point point = new Point();
			mapView.getProjection().toPixels(positionPin.getGeoPoint(), point);
			Bitmap bitmap = positionPin.getImage();
			canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2, 
					point.y - bitmap.getHeight() / 2, null);
		}		
	}
	
	public GeoPoint getStartPoint() {
		return positionPins.get(PinState.START).getGeoPoint();
	}
	
	public GeoPoint getEndPoint() {
		return positionPins.get(PinState.END).getGeoPoint();
	}
	
	public void setpinState(PinState pinState) {
		this.pinState = pinState; 
	}

	@Override
	public void onTap(GeoPoint geoPoint, MapView mapView) {
		if (pinState == PinState.START || pinState == PinState.END) {
			positionPins.get(pinState).setGeoPoint(geoPoint);
		}
		drawer.reDraw();
	}
	
}
