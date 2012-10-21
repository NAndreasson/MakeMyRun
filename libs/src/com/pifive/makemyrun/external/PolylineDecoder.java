package com.pifive.makemyrun.external;


import java.util.ArrayList;
import java.util.List;

import com.pifive.makemyrun.geo.Location;
/**
 * Originally belongs to Ismail Habib @
 * http://www.geekyblogger.com/2010/12/decoding-polylines-from-google-maps.html
 * 
 * Decodes a polyline from Google Directions to GeoPoints
 *
 */
public abstract class PolylineDecoder {
	
	/**
	 * Decodes provided String to a list of Locations
	 * @param encoded String containing an encoded polyline
	 * @return Returns a list of Locations containing geographical points.
	 */
	public static List<Location> decodePoly(String encoded) {
		List<Location> poly = new ArrayList<Location>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;
			Location p = new Location((double)(lat/1E5),(double)(lng/1E5));
			poly.add(p);
		}
		return poly;
	}
}