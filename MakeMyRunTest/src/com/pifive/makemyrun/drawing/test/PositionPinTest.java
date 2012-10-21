/*     Copyright (c) 2012 Johannes Wikner, Anton Lindgren, Victor Lindhe,
 *         Niklas Andreasson, John Hult
 *
 *     Licensed to the Apache Software Foundation (ASF) under one
 *     or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 */

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
