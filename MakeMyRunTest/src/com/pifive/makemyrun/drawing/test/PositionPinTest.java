package com.pifive.makemyrun.drawing.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.maps.GeoPoint;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.R;
import com.pifive.makemyrun.drawing.PositionPin;

public class PositionPinTest extends ActivityInstrumentationTestCase2<MainActivity>  {

	private PositionPin positionPin;
	
	public PositionPinTest() {
		super(MainActivity.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void testGetImage() {
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		int startPointLat = 22;
		int startPointLong = 22;
		GeoPoint startPoint = new GeoPoint(startPointLat, startPointLong);
		positionPin = new PositionPin(startPoint, pinBitmap);	
		
		assertEquals("Verify that the bitmap is equal", pinBitmap, positionPin.getImage());
	}
	
	public void testGetSetGeoPoint() {
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		int startPointLat = 22;
		int startPointLong = 22;
		GeoPoint startPoint = new GeoPoint(startPointLat, startPointLong);
		positionPin = new PositionPin(startPoint, pinBitmap);	
		
		assertEquals("Verify that the position is equal to the one instantiated", 
												startPoint, positionPin.getGeoPoint());
		
		int newPointLat = 55;
		int newPointLong = 65;
		GeoPoint newPoint = new GeoPoint(newPointLat, newPointLong);
		positionPin.setGeoPoint(newPoint);
		assertEquals("Verify that the position is equal to the new one set",
									newPoint, positionPin.getGeoPoint());

	}	
}
