package com.pifive.makemyrun.drawing.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

import android.graphics.Canvas;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.R;
import com.pifive.makemyrun.drawing.Drawer;
import com.pifive.makemyrun.drawing.PositionPin;
import com.pifive.makemyrun.drawing.PositionPlacerArtist;
import com.pifive.makemyrun.drawing.PositionPlacerArtist.PinState;

public class PositionPlacerArtistTest  extends ActivityInstrumentationTestCase2<MainActivity> {
	
	private PositionPlacerArtist positionPlacerArtist;
	private MapView mapView;
	private MockDrawer drawer = new MockDrawer();
	
	private int startPointLat = 22;
	private int startPointLong = 22;
	private int endPointLat = 55;
	private int endPointLong = 55;
	
	public PositionPlacerArtistTest() {
		super(MainActivity.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		GeoPoint startPoint = new GeoPoint(startPointLat, startPointLong);
		PositionPin startPin = new PositionPin(startPoint, pinBitmap);
		
		GeoPoint endPoint = new GeoPoint(endPointLat, endPointLong);
		PositionPin endPin = new PositionPin(endPoint, pinBitmap);
		
		positionPlacerArtist = new PositionPlacerArtist(startPin, endPin, drawer);
		
		// We don't need to test with correct key 
		// since we don't need to draw the actual map
		mapView = (MapView) getActivity().
						findViewById(com.pifive.makemyrun.R.id.mapview);
	}
	
	
	public void testGetSetPoint() {
		GeoPoint startPoint = new GeoPoint(startPointLat, startPointLong);
		
		String assertStartPointMessage = "Verify that the start GeoPoint is equal to the intial one";
		assertEquals(assertStartPointMessage, 
				startPoint.getLatitudeE6(), positionPlacerArtist.getStartPoint().getLatitudeE6());
		assertEquals(assertStartPointMessage, 
				startPoint.getLongitudeE6(), positionPlacerArtist.getStartPoint().getLongitudeE6());
		
		GeoPoint endPoint = new GeoPoint(endPointLat, endPointLong);
		
		String assertEndPointMessage = "Verify that the end GeoPoint is equal to the intial one";
		assertEquals(assertEndPointMessage, 
				endPoint.getLatitudeE6(), positionPlacerArtist.getEndPoint().getLatitudeE6());
		assertEquals(assertEndPointMessage, 
				endPoint.getLongitudeE6(), positionPlacerArtist.getEndPoint().getLongitudeE6());
	
		
		int newStartPointLat = 66;
		int newStartPointLong = 55;
		GeoPoint newStartPoint = new GeoPoint(newStartPointLat, newStartPointLong);
		positionPlacerArtist.setPinState(PinState.START);
		positionPlacerArtist.onTap(newStartPoint, mapView);
		
		String assertNewStartPointMessage = "Verify that the start GeoPoint is equal to the new one that has been set";
		assertEquals(assertNewStartPointMessage, 
				newStartPoint.getLatitudeE6(), positionPlacerArtist.getStartPoint().getLatitudeE6());
		assertEquals(assertNewStartPointMessage, 
				newStartPoint.getLongitudeE6(), positionPlacerArtist.getStartPoint().getLongitudeE6());
		
		int newEndPointLat = 77;
		int newEndPointLong = 77;
		GeoPoint newEndPoint = new GeoPoint(newEndPointLat, newEndPointLong);
		positionPlacerArtist.setPinState(PinState.END);
		positionPlacerArtist.onTap(newEndPoint, mapView);
		
		String assertNewEndPointMessage = "Verify that the end GeoPoint is equal to the new one that has been set";
		assertEquals(assertNewEndPointMessage, 
				newEndPoint.getLatitudeE6(), positionPlacerArtist.getEndPoint().getLatitudeE6());
		assertEquals(assertNewEndPointMessage, 
				newEndPoint.getLongitudeE6(), positionPlacerArtist.getEndPoint().getLongitudeE6());
	}
	
	public void testDraw() {
		positionPlacerArtist.draw(new Canvas(), mapView, false);
	}
	
	/**
	 * Mocked Drawer only for testing if redraw has been called.
	 */
	private class MockDrawer implements Drawer {
		
		private boolean drawRequested = false;
		
		/**
		 * Returns true if the reDraw() methofd has been called since
		 * the last call on this method. Which then also resets request status.
		 * @return Returns true if reDraw() has been called since last call of this method.
		 */
		protected boolean isDrawRequested() {
			boolean returnVal = drawRequested;
			drawRequested = false;
			return returnVal;
		}

		@Override
		public void reDraw() {
			drawRequested = true;
		}
	}
}
