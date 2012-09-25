package com.pifive.makemyrun;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        MapView map = new MapView(this, "0jk9toza2GMaBQEe--jPaMvm_2g17l0hP_xnXfw");
        setContentView(map);
        
        GenerateRouteTask routeTask = new GenerateRouteTask();
        try {
        	routeTask.execute();
        	JSONObject route = routeTask.get();
        
        	Log.i("Dev", route.toString());
        	
        } catch (ExecutionException e) {
        	e.printStackTrace();
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
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
