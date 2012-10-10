package com.pifive.makemyrun.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MMRDbAdapter {

	private static final String KEY_ID = "id";
	private static final String KEY_POLYLINE = "polyline";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIMERAN = "timeRan";
	private static final String KEY_DISTANCERAN = "distanceRan";
	private static final String KEY_COMPLETED = "completed";

	private static final String TAG = "MMR-"
			+ MMRDbAdapter.class.getSimpleName();

	private DatabaseHelper dbHelper;
	private SQLiteDatabase mmrDb;

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "routes";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table routes ( id integer primary key autoincrement, "
			+ "polyline string not null, date integer, timeRan integer, "
			+ "distanceRan integer, completed integer);";

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
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
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
	 * Closes our database helper object
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Creates a table with our information
	 * 
	 * @param polyline
	 *            The path coded as a polyline
	 * @param timeRan
	 *            The time when route was saved
	 * @param distanceRan
	 *            The distance ran
	 * @param wasCompleted
	 *            Completed/Cancelled
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long createRoute(String polyline, int timeRan, int distanceRan,
			boolean wasCompleted) {
		ContentValues values = new ContentValues();
		values.put(KEY_POLYLINE, polyline);
		values.put(KEY_DATE, System.currentTimeMillis() / 1000);
		values.put(KEY_TIMERAN, timeRan);
		values.put(KEY_DISTANCERAN, distanceRan);
		values.put(KEY_COMPLETED, wasCompleted ? 1 : 0);

		return mmrDb.insert(DATABASE_TABLE, null, values);

	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteNote(long rowId) {

		return mmrDb.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllRoutes() {
		return mmrDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_POLYLINE,
				KEY_DATE, KEY_TIMERAN, KEY_DISTANCERAN, KEY_COMPLETED }, null,
				null, null, null, KEY_DATE + " DESC");
	}

	public Cursor fetchRoute(long rowId) throws SQLException {
		Cursor cursor = mmrDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ID, KEY_POLYLINE, KEY_DATE, KEY_TIMERAN, KEY_DISTANCERAN,
				KEY_COMPLETED }, KEY_ID + "=" + rowId, null, null, null, null, 
				null);
		return cursor;
	}
}
