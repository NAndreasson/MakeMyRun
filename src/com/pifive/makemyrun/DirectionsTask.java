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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.pifive.makemyrun.database.MMRDbAdapter;
import com.pifive.makemyrun.model.RouteGenerationFailedException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Automatically requests directions by a query, default through Google
 * Directions. Response is converted to JSON and returned by user calling .get()
 * 
 * 
 */
public class DirectionsTask extends AsyncTask<String, Integer, JSONObject> {

	public static final String GOOGLE_URL = "http://maps.googleapis.com/maps/api/directions/json?";
	public static final String TEST_QUERY = "origin=Friggagatan,Gothenburg,Sweden&destination=Ran%C3%A4ngsgatan,Gothenburg,Sweden&mode=walking&sensor=false";
	public static final String GOOGLE_QUERY_ERROR = "REQUEST_DENIED";
	public static final String GOOGLE_QUERY_SUCCESS = "OK";
	private static final String TAG = "MMR-"
			+ DirectionsTask.class.getSimpleName();
	
	private final String loadingMessage;
	private final String finishedMessage;
	private final String restAPI;
	private LoadingStatus loadingStatus;
	
	/**
	 * A constructor which enables us to create task with custom host API.
	 * WARNING: This class is mainly implemented to match Google Directions API,
	 * using it with other restful APIs will probably fail.
	 * @param restAPI The API to contact for each request.
	 */
	public DirectionsTask(Context context, String restAPI) {
		this.restAPI = restAPI;
		loadingMessage = context.getResources().getString(R.string.directions_loading_message);
		finishedMessage = context.getResources().getString(R.string.directions_finished_message);
	}
	
	/**
	 * Adds a loadingStatus ProgressDialog. 
	 * You may want to run addItem() from here
	 * @param loadingStatus a loadingStatus.
	 */
	public void setLoadingStatus(LoadingStatus loadingStatus){
		this.loadingStatus = loadingStatus;
		this.loadingStatus.addItem();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * sets loading message for LoadingStatus
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		updateLoadingStage(false);
	}
	
	/**
	 * Updates LoadingStatus if one is set.
	 * @param message 
	 * 				Message to be shown
	 * @param finishedMessage
	 * 				Set to true if the task is completed
	 */
	private void updateLoadingStage(boolean finishedMessage) {
		if(loadingStatus != null){
			loadingStatus.setLoadingStage(loadingMessage, finishedMessage);
		}
		
	}

	/**
	 * Adds an abstractionlayer to the super.get() method
	 * and returns its result or an empty JSONObject if we're interrupted.
	 * @return
	 */
	public JSONObject simpleGet(String query) {
		execute(query);
		JSONObject obj = new JSONObject();
		int responseTimeout = 10;
		try {
        	obj = get(responseTimeout, TimeUnit.SECONDS);
        	
    	// We have already handled printing of user-errors. Flood the log!
        } catch (Exception e) {
        	cancel(true);
        	throw new RouteGenerationFailedException("Couldn't reach Google");
        }
        return obj;
	}

	@Override
	/**
	 * Performs a google REST request with provided query
	 * @param query Optional query to provide to the google request
	 * @return Returns a google reponse parsed as JSON
	 * or an empty JSONObjectif we fail to parse JSON from REST response.
	 */
	protected JSONObject doInBackground(String... query) {
		String myQuery = TEST_QUERY;
		if (query.length == 1) {
			myQuery = query[0];
		}

		JSONObject json = new JSONObject();
		try {
			URL url = new URL(restAPI + myQuery);

			String googleString = requestData(url);

			json = parseJSONString(googleString);
		} catch (Exception e) {
			cancel(true);
			throw new RouteGenerationFailedException(e.getMessage());
		}
		return json;
	}

	/**
	 * Performs a http request to a REST API for a JSON string.
	 * 
	 * @param url
	 *            The url to perform the request to.
	 * @return Returns a string for parsing to JSON.
	 */
	private String requestData(URL url) {
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			// Send request to REST API
			Log.d(TAG, url.getPath());
			connection = (HttpURLConnection) url.openConnection();
			Log.i(TAG,
					"Google response HTTP status: "
							+ connection.getResponseMessage());
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			// Parse response to string
			String data;
			Log.d(TAG, "Parsing Google response stream");
			while ((data = in.readLine()) != null) {
				stringBuilder.append(data);
			}
			// Catch IOException and close connection and stream.
		} catch (IOException e) {
			throw new RouteGenerationFailedException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					//Do nothing
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Parses a JSON string to a JSONObject and converts JSON Exceptions to
	 * our abstraction of an Exception.
	 * @param string The string which should contain valid JSON formatted text.
	 * @return Returns a JSONObject parsed from the string sent in.
	 */
	public JSONObject parseJSONString(String string) throws DirectionsException {
		try {
			JSONObject json = new JSONObject(string);

			Log.i(TAG, "Google response stream parsed successfully to JSON");

			String status = json.getString("status");
			Log.d(TAG, "Google response status: " + status);

			// If google can't handle it we never want to send it forward
			if (status.equals(GOOGLE_QUERY_ERROR)) {
				throw new DirectionsException(
						"Google returned error REQUEST_INVALID");
			}
			
			return json;
		} catch (JSONException e) {
			throw new DirectionsException("Response from Google was invalid JSON");
		}
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		updateLoadingStage(true);
	}
	
	
}
