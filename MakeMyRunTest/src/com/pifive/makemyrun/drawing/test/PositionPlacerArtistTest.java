package com.pifive.makemyrun.drawing.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

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
	
	public PositionPlacerArtistTest() {
		super(MainActivity.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		GeoPoint startPoint = new GeoPoint(22, 22);
		PositionPin startPin = new PositionPin(startPoint, pinBitmap);
		
		GeoPoint endPoint = new GeoPoint(55, 55);
		PositionPin endPin = new PositionPin(endPoint, pinBitmap);
		
		positionPlacerArtist = new PositionPlacerArtist(startPin, endPin, drawer);
		
		// We don't need to test with correct key 
		// since we don't need to draw the actual map
		mapView = (MapView) getActivity().
						findViewById(com.pifive.makemyrun.R.id.mapview);
	}
	
	
	public void testGetSetPoint() {
		GeoPoint startPoint = new GeoPoint(22, 22);
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				startPoint.getLatitudeE6(), positionPlacerArtist.getStartPoint().getLatitudeE6());
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				startPoint.getLongitudeE6(), positionPlacerArtist.getStartPoint().getLongitudeE6());
		
		GeoPoint endPoint = new GeoPoint(55, 55);
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				endPoint.getLatitudeE6(), positionPlacerArtist.getEndPoint().getLatitudeE6());
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				endPoint.getLongitudeE6(), positionPlacerArtist.getEndPoint().getLongitudeE6());
	
		GeoPoint newStartPoint = new GeoPoint(66, 55);
		positionPlacerArtist.setpinState(PinState.START);
		positionPlacerArtist.onTap(newStartPoint, mapView);
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				newStartPoint.getLatitudeE6(), positionPlacerArtist.getStartPoint().getLatitudeE6());
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				newStartPoint.getLongitudeE6(), positionPlacerArtist.getStartPoint().getLongitudeE6());
		
		GeoPoint newEndPoint = new GeoPoint(77, 77);
		positionPlacerArtist.setpinState(PinState.END);
		positionPlacerArtist.onTap(newEndPoint, mapView);
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				newEndPoint.getLatitudeE6(), positionPlacerArtist.getEndPoint().getLatitudeE6());
		assertEquals("Verify that the start GeoPoint is equal to the intial one", 
				newEndPoint.getLongitudeE6(), positionPlacerArtist.getEndPoint().getLongitudeE6());
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
