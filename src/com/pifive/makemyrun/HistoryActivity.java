package com.pifive.makemyrun;

import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.pifive.makemyrun.database.MMRDbAdapter;
import com.pifive.makemyrun.drawing.MapDrawer;

public class HistoryActivity extends MapActivity {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private MapView mapView;
	private View overlay;
	private MapDrawer mapDrawer;
	private MMRDbAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view correctly
		setContentView(R.layout.activity_history);
		mapView = (MapView) findViewById(R.id.mapview);
		overlay = findViewById(R.id.overlayMenu);
		mapDrawer = new MapDrawer(mapView);
		db = new MMRDbAdapter(getBaseContext());
		db.open();
		
	//	 * runs: id | routeId(reference to routes) | dateStart(start date in unix time seconds) |
//		 * dateEnd(end date in unix time seconds) | distanceRan | completed (1 = true, 0
//		 * = false)
		ListView list = (ListView) findViewById(R.id.historyList);
		Cursor cursor = db.fetchAllRunsJoinRoutes();
		CursorAdapter adapter = new CursorAdapter(getBaseContext(), cursor) {

			@Override
			public void bindView(View item, Context context, Cursor cursor) {
				// TODO Auto-generated method stub
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(getBaseContext(), ((TextView) v).getText(), Toast.LENGTH_LONG).show();
					}
					
				});
			}

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup list) {
				TextView view = new TextView(context);
				view.setTextAppearance(getBaseContext(), R.style.HistoryFont);
				
				Long millis = cursor.getLong(cursor.getColumnIndex("dateStart"));
				Date startDate = new Date(millis);
				int distanceRan = cursor.getInt(cursor.getColumnIndex(MMRDbAdapter.KEY_RUN_DISTANCE_RAN));
				int routeDistance = cursor.getInt(cursor.getColumnIndex(MMRDbAdapter.KEY_ROUTE_DISTANCE));
				
				view.setText(
						startDate.toString() + 
						" Distance: " +  
						distanceRan +
						" / " + routeDistance);
				
				return view;
			}
			
		};
		list.setAdapter(adapter);
		
		db.close();
		DataSetObserver observer = new DataSetObserver() {};
		
		
//		gestureDetector = new GestureDetector(mapView.getContext(),
//				new HistoryGestureDetector());
//		gestureListener = new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent motionEvent) {
//				return gestureDetector.onTouchEvent(motionEvent);
//			}
//		};
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

//	public class HistoryGestureDetector extends SimpleOnGestureListener {
//
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//				float velocityY) {
//
//			// Do nothing if we swipe diagonally
//			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//				return false;
//			
//			// Left swipe
//			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//				Toast.makeText(HistoryActivity.this, "Left Swipe",
//						Toast.LENGTH_SHORT).show();
//				// See below
//				
//				
//			// Right swipe
//			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//				
//				// Here we wish to start displaying a new run
//				// Which means:
//				
//				// a) Offset UI for timer and distance for both this and the to-be displayed run
//				
//				// b) Start animating the MapView towards new target run
//				Toast.makeText(HistoryActivity.this, "Right Swipe",
//						Toast.LENGTH_SHORT).show();
//			}
//			return false;
//		}
//
//	}
	
}
