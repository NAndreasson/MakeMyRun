package com.pifive.makemyrun.test;

import com.pifive.makemyrun.Location;

import junit.framework.TestCase;

public class LocationTest extends TestCase {

	private Location testLoc = new Location(57.043, 31.32);
	
	
	public void testGetLng(){
		assertTrue(testLoc.getLat() == 57.043);
	}
	
	public void testGetLat(){
		assertTrue(testLoc.getLng() == 31.32);
	}
	
	public void testGetMicroLng(){
		assertTrue(testLoc.getMicroLat() == 57.043*1E6);
	}
	
	public void testGetMicroLat(){
		assertTrue(testLoc.getMicroLng() == 31.32*1E6);
	}
	
	public void testToString(){
		assertEquals(testLoc.toString(),"Location [lat=57.043, lng=31.32]");
	}
}
