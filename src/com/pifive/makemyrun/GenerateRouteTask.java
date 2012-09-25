package com.pifive.makemyrun;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class GenerateRouteTask extends AsyncTask<String, Integer, JSONObject>{
	
	private final static String DEFAULT_QUERY = "origin=Toronto&destination=Montreal&sensor=false";
	
	@Override
	/**
	 * 
	 */
	protected void onPreExecute() {
		Log.i("Dev", "Pre exec");
	}

	@Override
	/**
	 * 
	 */
	protected JSONObject doInBackground(String... query) {
		String myQuery = DEFAULT_QUERY;
		if (query.length == 1) {
			myQuery = query[0];
		}
		
		try {
			return contactGoogle(myQuery);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	private JSONObject contactGoogle(String query) {
		JSONObject json = null;
		try {
			URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?" + query);			
			HttpURLConnection connection = null;
			try {  	
				connection = (HttpURLConnection) url.openConnection();
				Reader in = new InputStreamReader(connection.getInputStream());

				int data = 0;
				String string = "";
				
				while (data != -1) {
					data = in.read();
					if (data != -1) {
						string = string + (char) data; 
					}
				}

				try {
					json = new JSONObject(string);					
				} catch (JSONException e) {	
					e.printStackTrace();
				} finally {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return json;
	}
}
