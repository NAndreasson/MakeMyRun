package com.pifive.makemyrun.test;

import java.util.LinkedList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.pifive.makemyrun.Location;
import com.pifive.makemyrun.MainActivity;
import com.pifive.makemyrun.RouteDrawer;

public class RouteDrawerTest extends ActivityInstrumentationTestCase2<MainActivity>{

	public RouteDrawerTest() {
		super(MainActivity.class);
	}

	private RouteDrawer drawer;
	private MapView mapView;
	private List<Overlay> overlays;
	private List<Location> list = new LinkedList<Location>();

	/**
	 * Sets up MapView with Overlays and mocks a location list.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		// Mock locations
		list.add(new Location(1.0, 2.0));
		list.add(new Location(1.1, 2.0));
		
		mapView = (MapView) getActivity().findViewById(com.pifive.makemyrun.R.id.mapview);
		overlays = mapView.getOverlays();
	}
	
	/**
	 * Tests that provided mapview gets a new overlay and that center is set from
	 * starting location for the list provided.
	 */
	public void testOverlayAdding() {
		drawer = new RouteDrawer(mapView, list);
		Overlay myOverlay = overlays.get(overlays.size() - 1);

		assertEquals("Verify that we have added a RouteOverlay after construction", 
				"class com.pifive.makemyrun.RouteDrawer$RouteOverlay",
				myOverlay.getClass().toString());
	}
	
	public void testMapCentering() {
		Location firstPoint = list.get(0);
		assertEquals("Verify that we have centered on startlocation", 
				new GeoPoint(firstPoint.getMicroLat(), firstPoint.getMicroLng()),
				mapView.getMapCenter());
	}
}
