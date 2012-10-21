/*     Copyright (c) 2012 Johannes Wikner, Anton Lindgren, Victor Lindhe,
 *         Niklas Andreasson, John Hult
 *
 *     Licensed to the Apache Software Foundation (ASF) under one
 *     or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 */

package com.pifive.makemyrun.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The MMRDbAdapter handles a database containing two tables: runs: id |
 * routeId(reference to routes) | dateStart(start date in unix time
 * milliseconds) | dateEnd(end date in unix time milliseconds) | distanceRan |
 * completed (1 = true, 0 = false)
 * 
 * routes: id | polyline (string containing encoded coordinates)
 * 
 * A run row refers to a route row which contains a polyline which holds
 * coordinates encrypted as a string
 */
public class MMRDbAdapter {

	// Route table
	public static final String DATABASE_TABLE_ROUTES = "routes";
	public static final String KEY_ROUTE_ID = "_id";
	public static final String KEY_ROUTE_POLYLINE = "polyline";
	public static final String KEY_ROUTE_DISTANCE = "distance";

	// Run table
	public static final String DATABASE_TABLE_RUNS = "runs";
	public static final String KEY_RUN_ID = "_id";
	public static final String KEY_RUN_ROUTE = "routeId";
	public static final String KEY_RUN_DATE_STARTED = "dateStart";
	public static final String KEY_RUN_DATE_COMPLETED = "dateEnd";
	public static final String KEY_RUN_DISTANCE_RAN = "distanceRan";
	public static final String KEY_RUN_COMPLETED = "completed";

	private static final String TAG = "MMR-"
			+ MMRDbAdapter.class.getSimpleName();

	private DatabaseHelper dbHelper;
	private SQLiteDatabase mmrDb;

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_RUNS = "create table runs ( "
			+ KEY_RUN_ID + " integer primary key autoincrement, "
			+ KEY_RUN_ROUTE + " integer, " + KEY_RUN_DATE_STARTED
			+ " integer, " + KEY_RUN_DATE_COMPLETED + " integer, "
			+ KEY_RUN_DISTANCE_RAN + " integer, " + KEY_RUN_COMPLETED
			+ " integer);";

	private static final String DATABASE_CREATE_ROUTES = "create table routes( "
			+ KEY_ROUTE_ID
			+ " integer primary key autoincrement,"
			+ KEY_ROUTE_POLYLINE
			+ " string not null, "
			+ KEY_ROUTE_DISTANCE
			+ " int);";

	private final Context context;

	/**
	 * Class that handles SQLite queries. Extends helper class that helps manage
	 * database creation and version
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_RUNS);
			db.execSQL(DATABASE_CREATE_ROUTES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS routes");
			db.execSQL("DROP TABLE IF EXISTS runs");
			onCreate(db);
		}
	}

	public MMRDbAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Open the routes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database can't be opened or created
	 */
	public MMRDbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		mmrDb = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * 
	 * @param polyline
	 * @return a polyline's corresponding id routes or -1 if non existent.
	 */
	private int getRouteIdFromPolyline(String polyline) {
		Cursor routeCursor = mmrDb.query(true, DATABASE_TABLE_ROUTES,
				new String[] { KEY_ROUTE_ID }, KEY_ROUTE_POLYLINE + "= '"
						+ polyline + "'", null, null, null, null, null);
		if (routeCursor.getCount() <= 0) {
			return -1;
		}
		routeCursor.moveToFirst();
		return routeCursor.getInt(0);
	}

	/**
	 * Creates a row with our information
	 * 
	 * @param route
	 *            A route polyline
	 * @param startTime
	 *            The time when route was started in ms since 00:00 01-01-1970
	 * @param distanceRan
	 *            The distance ran
	 * @param routeDistance
	 * 			  The route's distance
	 * @param wasCompleted
	 *            Completed/Cancelled
	 * @return the row ID of the newly inserted row, or -1 if an error
	 */
	public int createRun(String route, long startTime, int distanceRan,
			int routeDistance, boolean wasCompleted) {
		int routeId = getRouteIdFromPolyline(route);
		if (routeId == -1) {
			routeId = createRoute(route, routeDistance);

		}
		ContentValues values = new ContentValues();
		values.put(KEY_RUN_ROUTE, routeId);
		values.put(KEY_RUN_DATE_STARTED, startTime);
		values.put(KEY_RUN_DATE_COMPLETED, System.currentTimeMillis());
		values.put(KEY_RUN_DISTANCE_RAN, distanceRan);
		values.put(KEY_RUN_COMPLETED, wasCompleted ? 1 : 0);
		return (int) mmrDb.insert(DATABASE_TABLE_RUNS, null, values);

	}

	private int createRoute(String polyline, int distanceRan) {
		ContentValues routeValue = new ContentValues();
		routeValue.put(KEY_ROUTE_POLYLINE, polyline);
		routeValue.put(KEY_ROUTE_DISTANCE, distanceRan);
		return (int) mmrDb.insert(DATABASE_TABLE_ROUTES, null, routeValue);

	}

	/**
	 * Delete the route with the given rowId
	 * 
	 * @param rowId
	 *            id of route to delete
	 * @return true if deleted, false otherwise
	 */
	private boolean deleteRoute(int rowId) {
		return mmrDb.delete(DATABASE_TABLE_ROUTES, KEY_ROUTE_ID + "=" + rowId,
				null) > 0;
	}

	/**
	 * Delete the run with the given rowId
	 * 
	 * @param rowId
	 *            id of route to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteRun(int rowId) {

		Cursor run = fetchRun(rowId);
		if(run.getCount() > 0){
			run.moveToFirst();
			int routeId = run.getInt(run.getColumnIndex(MMRDbAdapter.KEY_RUN_ROUTE));
			Cursor routes = mmrDb.query(DATABASE_TABLE_RUNS, new String[]{KEY_RUN_ROUTE},KEY_RUN_ROUTE + "="+routeId , null, null, null, null);
			if (routes.getCount() == 1){
				Log.d(TAG, "im removing nao!");
				deleteRoute(routeId);
			}
		}
		
		return mmrDb.delete(DATABASE_TABLE_RUNS, KEY_RUN_ID + "="
				+ rowId, null) > 0;
	}

	/**
	 * 
	 * @return all routes.
	 */
	public Cursor fetchAllRoutes() {
		return mmrDb.query(DATABASE_TABLE_ROUTES, new String[] { KEY_ROUTE_ID,
				KEY_ROUTE_POLYLINE, KEY_ROUTE_DISTANCE }, null, null, null,
				null, null);
	}

	/**
	 * 
	 * @param rowId
	 *            route id
	 * @return a specific route.
	 * @throws SQLException
	 *             if there was an error
	 */
	public Cursor fetchRoute(int rowId) throws SQLException {
		Cursor cursor = mmrDb.query(true, DATABASE_TABLE_ROUTES, new String[] {
				KEY_ROUTE_ID, KEY_ROUTE_POLYLINE, KEY_ROUTE_DISTANCE },
				KEY_ROUTE_ID + "=" + rowId, null, null, null, null, null);
		return cursor;
	}

	/**
	 * Selected table will look like : runId | routeId | dateStart | dateEnd |
	 * distance | wasCompleted | polyline
	 * 
	 * @return All runs along with corresponding route id and polyline
	 */
	public Cursor fetchAllRunsJoinRoutes() {
		String allExceptRouteId = DATABASE_TABLE_RUNS + "." + KEY_RUN_ID + ","
				+ KEY_RUN_ROUTE + "," + KEY_RUN_DATE_STARTED + ","
				+ KEY_RUN_DATE_COMPLETED + "," + KEY_RUN_DISTANCE_RAN + ","
				+ KEY_RUN_COMPLETED + "," + KEY_ROUTE_POLYLINE + ","
				+ KEY_ROUTE_DISTANCE;
		String query = "SELECT " + allExceptRouteId + " FROM "
				+ DATABASE_TABLE_RUNS + " INNER JOIN " + DATABASE_TABLE_ROUTES
				+ " ON " + DATABASE_TABLE_RUNS + "." + KEY_RUN_ROUTE + "="
				+ DATABASE_TABLE_ROUTES + "." + KEY_ROUTE_ID + ";";

		return mmrDb.rawQuery(query, null);
	}

	/**
	 * 
	 * @param rowId
	 *            a run's id
	 * @return a specific run with its corrosponding route id and polyline
	 */
	public Cursor fetchRunJoinRoute(int rowId) {
		String allExceptRouteId = DATABASE_TABLE_RUNS + "." + KEY_RUN_ID + ","
				+ KEY_RUN_ROUTE + "," + KEY_RUN_DATE_STARTED + ","
				+ KEY_RUN_DATE_COMPLETED + "," + KEY_RUN_DISTANCE_RAN + ","
				+ KEY_RUN_COMPLETED + "," + KEY_ROUTE_POLYLINE + ","
				+ KEY_ROUTE_DISTANCE;

		String query = "SELECT " + allExceptRouteId + " FROM "
				+ DATABASE_TABLE_RUNS + " INNER JOIN " + DATABASE_TABLE_ROUTES
				+ " ON " + DATABASE_TABLE_RUNS + "." + KEY_RUN_ROUTE + "="
				+ DATABASE_TABLE_ROUTES + "." + KEY_ROUTE_ID + " WHERE "
				+ DATABASE_TABLE_RUNS + "." + KEY_RUN_ID + "=" + rowId + ";";
		return mmrDb.rawQuery(query, null);
	}

	/**
	 * This returns all run rows. To get the routes that the runs are referring
	 * to, use fetchAllRunsJoinRoutes
	 * 
	 * @return All runs
	 */
	public Cursor fetchAllRuns() {
		return mmrDb.query(DATABASE_TABLE_RUNS, new String[] { KEY_RUN_ID,
				KEY_RUN_ROUTE, KEY_RUN_DATE_STARTED, KEY_RUN_DATE_COMPLETED,
				KEY_RUN_DISTANCE_RAN, KEY_RUN_COMPLETED }, null, null, null,
				null, KEY_RUN_DATE_COMPLETED + " DESC");
	}

	/**
	 * 
	 * @param rowId
	 *            a run's unique id
	 * @return a run. use fetchRunJoinRoute to get the polyline of the route
	 *         that the run is referring to
	 * @throws SQLException
	 *             if there was an error
	 */
	public Cursor fetchRun(int rowId) throws SQLException {
		Cursor cursor = mmrDb.query(true, DATABASE_TABLE_RUNS,
				new String[] { KEY_RUN_ID, KEY_RUN_ROUTE, KEY_RUN_DATE_STARTED,
						KEY_RUN_DATE_COMPLETED, KEY_RUN_DISTANCE_RAN,
						KEY_RUN_COMPLETED }, KEY_RUN_ID + "=" + rowId, null,
				null, null, null, null);
		return cursor;
	}
}
