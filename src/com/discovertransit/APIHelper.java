package com.discovertransit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APIHelper {

	public static JSONObject getJSONObject(String myURL) throws IOException, JSONException {
		JSONObject json = getData(myURL);
		return json.getJSONObject("data");
	}
	
	public static JSONArray getJSONArray(String myURL) throws IOException, JSONException {
		JSONObject json = getData(myURL);
		return json.getJSONArray("data");
	}
	
	private static JSONObject getData(String myURL) throws IOException, JSONException {
		if(myURL==null)
			return null;
		URL myURLObject = new java.net.URL(myURL);
		InputStream is = myURLObject.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		return new JSONObject(br.readLine());
	}
}
