package com.pifive.makemyrun;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view correctly
		setContentView(R.layout.activity_history);
		mapView = (MapView) findViewById(R.id.mapview);
		overlay = findViewById(R.id.overlayMenu);
		mapDrawer = new MapDrawer(mapView);
		mapView.setClickable(false);

		gestureDetector = new GestureDetector(mapView.getContext(),
				new HistoryGestureDetector());
		gestureListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		};
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public class HistoryGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Toast.makeText(HistoryActivity.this, "Left Swipe",
						Toast.LENGTH_SHORT).show();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Toast.makeText(HistoryActivity.this, "Right Swipe",
						Toast.LENGTH_SHORT).show();
			}
			return false;
		}

	}

}
