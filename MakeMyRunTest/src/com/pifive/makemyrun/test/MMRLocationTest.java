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

package com.pifive.makemyrun.test;

import com.pifive.makemyrun.geo.MMRLocation;

import junit.framework.TestCase;

public class MMRLocationTest extends TestCase {

	private MMRLocation testLoc = new MMRLocation(57.043, 31.32);
	
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
