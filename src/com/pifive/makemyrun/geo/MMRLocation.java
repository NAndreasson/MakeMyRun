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

package com.pifive.makemyrun.geo;

/**
 * A location defined by a latitude and longitue angles
 */
public class MMRLocation {

	private double lat;
	private double lng;
	
	/**
	 * Creates a new location object
	 * @param lat Latitude degrees
	 * @param lng Longitude degrees
	 */
	public MMRLocation(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	/**
	 * 
	 * @return Latitude value
	 */
	public double getLat(){
		return lat;
	}
	
	/**
	 * 
	 * @return Longitude value
	 */
	public double getLng(){
		return lng;
	}
	
	/**
	 * 
	 * @return Latitude value in micro-degrees
	 */
	public int getMicroLat(){
		return (int) (lat*1E6);
	}
	
	/**
	 * 
	 * @return Longitude value in micro-degrees
	 */
	public int getMicroLng(){
		return (int) (lng*1E6);
	}

	/**
	 * retruns a string formated like: "Location [lat=xx.x, lng=yy.y]"
	 */
	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}
