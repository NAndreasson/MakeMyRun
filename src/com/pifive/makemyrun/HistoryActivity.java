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
