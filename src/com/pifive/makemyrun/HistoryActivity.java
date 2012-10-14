package com.pifive.makemyrun;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

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
		
		//Create and set an adapter
		RunHistory adapter = new RunHistory(getBaseContext(), cursor);
		list.setAdapter(adapter);
		
		//Close database
		db.close();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
