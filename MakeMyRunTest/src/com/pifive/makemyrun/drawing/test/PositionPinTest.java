package com.pifive.makemyrun.drawing.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.maps.GeoPoint;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.R;
import com.pifive.makemyrun.drawing.PositionPin;

public class PositionPinTest extends ActivityInstrumentationTestCase2<MainActivity>  {
	private int testPoint = 22;
	private int testLat = 55;
	private int testLong = 65;
	
	private PositionPin positionPin;
	
	public PositionPinTest() {
		super(MainActivity.class);
	}
	
	public void testGetImage() {
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		GeoPoint startPoint = new GeoPoint(testPoint, testPoint);
		positionPin = new PositionPin(startPoint, pinBitmap);	
		
		assertEquals("Verify that the bitmap is equal", pinBitmap, positionPin.getImage());
	}
	
	public void testGetSetGeoPoint() {
		Bitmap pinBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pin);
		
		GeoPoint startPoint = new GeoPoint(testPoint, testPoint);
		positionPin = new PositionPin(startPoint, pinBitmap);	
		
		assertEquals("Verify that the position is equal to the one instantiated", 
												startPoint, positionPin.getGeoPoint());
		
		GeoPoint newPoint = new GeoPoint(testLat, testLong);
		positionPin.setGeoPoint(newPoint);
		assertEquals("Verify that the position is equal to the new one set",
									newPoint, positionPin.getGeoPoint());

	}	
}
