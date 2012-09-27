package com.pifive.makemyrun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;


public class DirectionsTask extends AsyncTask<String, Integer, JSONObject>{
	
	private final static String GOOGLE_URL = "http://maps.googleapis.com/maps/api/directions/json?";
	private final static String DEFAULT_QUERY = "origin=Stockholm&destination=Gothenburg&sensor=false";
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
			return json;
		} catch (MalformedURLException e) {
			cancel(false);
			cancelCause = R.string.url_format_failed;
		} catch (JSONException e) {
			cancel(false);
			cancelCause = R.string.json_parse_failed;
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
			connection = (HttpURLConnection) url.openConnection();
			Log.i("MMR", "Google response HTTP status: " + connection.getResponseMessage());
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String data;
			Log.d("MMR", "Parsing Google response stream");
			while ((data = in.readLine()) != null) {
				stringBuilder.append(data);
			}
		} catch (IOException e) {
			cancel(false);
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
	
	@Override
	protected void onCancelled() {
		// Return to generation view and print error message
	}
}
