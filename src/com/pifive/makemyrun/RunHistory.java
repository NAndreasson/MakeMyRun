package com.pifive.makemyrun;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pifive.makemyrun.database.MMRDbAdapter;

public class RunHistory extends CursorAdapter {	
	
	public RunHistory(Context context, Cursor c) {
		super(context, c);
	}
	
	/**
	 * Binds a toast just to make sure we can do something on click
	 */
	@Override
	public void bindView(View item, final Context context, Cursor cursor) {
		// TODO Bind to a more detailed mapview with flash graphics
				
	}

	/**
	 * Creates a simple textview for each listitem.
	 * Displays ISO-format Date together with distance run/routedistance
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup list) {
		TextView view = new TextView(context);
		view.setTextAppearance(context, R.style.HistoryFont);
		
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
	
}
