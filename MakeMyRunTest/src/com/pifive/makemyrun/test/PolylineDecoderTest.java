package com.pifive.makemyrun.test;

import java.util.List;

import junit.framework.TestCase;

import com.pifive.makemyrun.Location;
import com.pifive.makemyrun.PolylineDecoder;

public class PolylineDecoderTest extends TestCase{
	
	private final String testPoints = "}sf_JmgdhAa@cA_@aAKSoCwGcAqCg@qA";
	
	public void testDecode(){
		List<Location> result = PolylineDecoder.decodePoly(testPoints);
		
		assertTrue(result != null && result.size() > 0);
		assertTrue(result.get(0).getLat()+result.get(0).getLng() != 0);
	}

}
