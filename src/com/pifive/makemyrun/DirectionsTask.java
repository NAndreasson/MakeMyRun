package com.pifive.makemyrun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class DirectionsTask extends AsyncTask<String, Integer, JSONObject>{
	
	private final static String GOOGLE_URL = "http://maps.googleapis.com/maps/api/directions/json?";
	private final static String DEFAULT_QUERY = "origin=Stockholm&destination=Gothenburg&sensor=false";
	private final static String GOOGLE_QUERY_ERROR = "REQUEST_DENIED";
	
	private int cancelCause;

	@Override
	/**
	 * Performs a google REST request with provided query
	 * @param query Optional query to provide to the google request
	 * @return Returns a google reponse parsed as JSON or null if we fail to parse JSON
	 */
	protected JSONObject doInBackground(String... query) {
		String myQuery = DEFAULT_QUERY;
		if (query.length == 1) {
			myQuery = query[0];
		}
		
		try {
			URL url = new URL(GOOGLE_URL + myQuery);
			
			String googleString = getGoogleData(url);
			JSONObject json = new JSONObject(googleString);
			
			Log.i("MMR", "Google response stream parsed successfully to JSON");
			
			String googleStatus = json.getString("status");
			Log.d("MMR", "Google response status: " + googleStatus);
			
			// If google can't handle it we never want to send it forward
			if (googleStatus.equals(GOOGLE_QUERY_ERROR)) {
				throw new DirectionsException("Google returned error REQUEST_INVALID");
			}
			return json;
		} catch (MalformedURLException e) {
			cancelCause = R.string.url_format_failed;
			cancel(true);
		} catch (JSONException e) {
			cancelCause = R.string.json_parse_failed;
			cancel(true);
		} catch (DirectionsException e) {
			cancelCause = R.string.google_rest_failed;
			cancel(true);
		}
		return null;
	}
	
	/**
	 * Performs a http request to google's webservice REST API for a directions JSON string
	 * @param url The url to perform the request to.
	 * @return Returns a string ready to be parsed to a JSONObject
	 */
	private String getGoogleData(URL url) {
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			// Send request to REST API
			connection = (HttpURLConnection) url.openConnection();
			Log.i("MMR", "Google response HTTP status: " + connection.getResponseMessage());
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			// Parse response to string
			String data;
			Log.d("MMR", "Parsing Google response stream");
			while ((data = in.readLine()) != null) {
				stringBuilder.append(data);
			}
		} catch (IOException e) {
			cancel(true);
			cancelCause = R.string.google_rest_failed;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();					
				} catch (IOException e) {}
			}
		}
		return stringBuilder.toString();
	}
	
	/**
	 * Returns the resource ID for the cancel message
	 * @return Returns an integer ID for the string resource
	 * describing cause of cancellation.
	 */
	public int getCancelCause() {
		return cancelCause;
	}
}
