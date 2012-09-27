package com.pifive.makemyrun;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        mc.animateTo(new GeoPoint(57685535, 11987197));
//        mc.setZoom(50);
        List list = new LinkedList<int[]>();
        list.add(new int[] {57685535, 11987197});
        list.add(new int[] {57685306, 11990930});
        list.add(new int[] {57684331, 11994342});
        new RouteDrawer(mapView, list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
