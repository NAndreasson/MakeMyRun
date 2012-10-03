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

import android.location.Location;
import android.test.AndroidTestCase;

import com.pifive.makemyrun.NoLocationException;
import com.pifive.makemyrun.RouteGenerator;

public class RouteGeneratorOtherTests extends
		AndroidTestCase {
	
	/**
	 * Should throw exception due to no location available in tests
	 */
	public void testGetCurrentRoute() {
		try {
			Location returnedLocation = RouteGenerator.getCurrentLocation(getContext());
			fail("No exception = fail");
		} catch (NoLocationException e) {
			assert(true);
		}
		
	}

}
