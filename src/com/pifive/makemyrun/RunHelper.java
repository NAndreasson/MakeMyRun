package com.pifive.makemyrun;

import java.util.List;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.database.MMRDbAdapter;
import com.pifive.makemyrun.drawing.CurrentLocationArtist;
import com.pifive.makemyrun.drawing.MapDrawer;
import com.pifive.makemyrun.drawing.RouteArtist;
import com.pifive.makemyrun.geo.MMRLocation;
import com.pifive.makemyrun.model.Route;
import com.pifive.makemyrun.model.RouteGenerator;

public class RunHelper {
	private Context context;
	private DistanceTracker distanceTracker;
	

	private Timer timer;
	private LoadingStatus loadingStatus;
	private GeoPoint startPoint; 
	private GeoPoint endPoint;
	private MapDrawer mapDrawer;	
	private MMRDbAdapter db;
	private Route currentRoute;
	private Activity activity;
	

	/**
	 * Constructor.
	 * Adds the layout to the Activity
	 * @param activity
	 */
	public RunHelper(final Activity activity, final MapView mapView) {
		this.activity = activity;
		context = activity.getBaseContext();
		mapDrawer = new MapDrawer(mapView);
        db = new MMRDbAdapter(context);
	}

    /**
	 * Constructs a MyLocationDrawer to draw current location.
	 */
	public void displayCurrentLocation() {
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Construct our location artist
		CurrentLocationArtist locationArtist = new CurrentLocationArtist(mapDrawer);
		mapDrawer.addArtist(locationArtist);
		
		// Make it aware of location updates from all providers
		List<String> providers = locManager.getAllProviders();
		for (String provider : providers) {
			locManager.requestLocationUpdates(
					provider,
					0, 
					0, 
					locationArtist);
		}
		Log.d("MMR", "Does this run? ");
	}
	
	public Location getCurrentLocation() {
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	
		// Fetch last known location to provide as best guess
		String provider = locManager.getBestProvider(new Criteria(), true);
		//locManager.getProvider(LocationManager.GPS_PROVIDER);
		Log.d("MMR", "im using provider : "+ provider);

		android.location.Location currentLocation = 
					locManager.getLastKnownLocation(provider);
    	
		if (currentLocation == null) {
			throw new NoLocationException("Location unavailable");
		}
		return currentLocation;
    }

	/**
	 * Saves a run
	 * @param completed
	 * @return
	 */
	public boolean saveRun(boolean completed) {
		Log.d("MMR", "Route distance: "+currentRoute.getDistance());
		Log.d("MMR", "Ran distance: " + distanceTracker.getTotalDistanceInMeters());
		db.open();
		boolean result = db.createRun(
				currentRoute.getPolyline(),
				timer.getStartTime(), 
				(int) distanceTracker.getTotalDistanceInMeters(), 
				currentRoute.getDistance(), 
				completed) != -1;

		db.close();
		return result; 
	}
	
	public DistanceTracker getDistanceTracker() {
		return distanceTracker;
	}

	public void setStartPoint(GeoPoint startPoint) {
		this.startPoint = startPoint;
	}

	public void setEndPoint(GeoPoint endPoint) {
		this.endPoint = endPoint;
	}

	public MapDrawer getMapDrawer() {
		return mapDrawer;
	}

	/**
	 * Constructs a DistanceTracker to track distance
	 */
	private void trackDistance() {
		// Get location manager from system
		LocationManager locManager = 
				(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	
		// Get the starting position
		String provider = LocationManager.GPS_PROVIDER;
		android.location.Location currentLocation = 
					locManager.getLastKnownLocation(provider);
			
		distanceTracker = new DistanceTracker(currentLocation);
		// set distanceTracker to retrieve location updates
		locManager.requestLocationUpdates(provider, 0, 0, getDistanceTracker());
	}
	
	/**
	 * Cleans up logic
	 */
	public void cleanUp() {
		mapDrawer.clearDrawer();
    	if (timer instanceof Timer) {
    		timer.stop();    		
    	}
	}

	public void startRunLogic(Observer observer) {
    	trackDistance();
        getDistanceTracker().addObserver(observer);

        timer = new Timer((TextView) activity.findViewById(R.id.clocktext));
    	timer.start();
	}

	public void generateRoute(MapView view) {
		loadingStatus = new LoadingStatus(view.getContext());
		try {
			String query = RouteGenerator.generateRoute(
							startPoint, endPoint);
			startDirectionsTask(query, view);
		} catch (RuntimeException e) {
			loadingStatus.remove();
			Log.d("MMR", e.getStackTrace().toString());
			Toast.makeText(context.getApplicationContext(), "ERROR: "+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return;
		}
	}
	

	/**
	 * Queries Google Directions with supplied query through a task.
	 * @param query The query to execute DirectionsTask with.
	 */
	private void startDirectionsTask(String query, MapView mapView) {
        DirectionsTask directionsTask = new DirectionsTask(context, DirectionsTask.GOOGLE_URL);
        directionsTask.setLoadingStatus(loadingStatus);
        JSONObject googleRoute = directionsTask.simpleGet(query);
        
        try {
			currentRoute = new Route(googleRoute);

			// Center on our starting point
			MMRLocation location = currentRoute.getWaypoints().get(0);
			GeoPoint geoPoint = new GeoPoint(
									location.getMicroLat(),
									location.getMicroLng());
			mapView.getController().animateTo(geoPoint);
			
			// Add an artist to draw our route
			RouteArtist routeArtist = new RouteArtist(currentRoute.getWaypoints());
			getMapDrawer().addArtist(routeArtist);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}