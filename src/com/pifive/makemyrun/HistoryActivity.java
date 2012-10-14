package com.pifive.makemyrun;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.pifive.makemyrun.database.MMRDbAdapter;

/**
 * Lists all previously saved runs in from the database.
 * A simple list displaying date and distance ran / distance of the route generated
 */
public class HistoryActivity extends MapActivity {

	private MMRDbAdapter db;

	/**
	 * Creates a list with all run entries in the database
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view correctly
		setContentView(R.layout.activity_history);
		
		// Open database
		db = new MMRDbAdapter(getBaseContext());
		db.open();
		
		// Get list 
		ListView list = (ListView) findViewById(R.id.historyList);
		Cursor cursor = db.fetchAllRunsJoinRoutes();
		
		
		CursorAdapter adapter = new CursorAdapter(getBaseContext(), cursor) {

			/**
			 * Binds a toast just to make sure we can do something on click
			 */
			@Override
			public void bindView(View item, Context context, Cursor cursor) {
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Bind to a more detailed mapview with flash graphics
						Toast.makeText(getBaseContext(), ((TextView) v).getText(), Toast.LENGTH_LONG).show();
					}
					
				});
			}

			/**
			 * Creates a simple textview for each listitem.
			 * Displays ISO-format Date together with distance run/routedistance
			 */
			@Override
			public View newView(Context context, Cursor cursor, ViewGroup list) {
				TextView view = new TextView(context);
				view.setTextAppearance(getBaseContext(), R.style.HistoryFont);
				
				Long millis = cursor.getLong(cursor.getColumnIndex("dateStart"));
				Date startDate = new Date(millis);
				
				// Distances
				int distanceRan = cursor.getInt(cursor.getColumnIndex(MMRDbAdapter.KEY_RUN_DISTANCE_RAN));
				int routeDistance = cursor.getInt(cursor.getColumnIndex(MMRDbAdapter.KEY_ROUTE_DISTANCE));
				
				view.setText(
						new SimpleDateFormat("yyyy-mm-dd").format(startDate) + 
						" Distance: " +  
						distanceRan +
						" / " + routeDistance);
				
				return view;
			}
			
		};
		list.setAdapter(adapter);
		
		db.close();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
