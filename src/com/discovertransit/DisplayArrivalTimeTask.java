package com.discovertransit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class DisplayArrivalTimeTask extends AsyncTask<String,Void,String>{

	private Context context;

	public DisplayArrivalTimeTask(Context context) {
		this.context = context;
	}
	@Override
	protected String doInBackground(String... input) {
		String stopURL = input[0];
		String time = null;
		if(stopURL!=null) {

			URL stopURLObject;
			try {
				stopURLObject = new java.net.URL(stopURL);
				InputStream is = stopURLObject.openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				JSONObject json = new JSONObject(br.readLine());
				json = json.getJSONObject("data");

				JSONArray times = json.getJSONArray("times");
				if(times.length()>0) {
					time = times.get(0).toString();
				}
			} catch (Exception e) {
				Toast.makeText(context, "Unable to retrieve arrival time. Please try again.", Toast.LENGTH_LONG).show();
			}
		}
		return time;
	}
	protected void onPreExecute() {
		Toast.makeText(context, "[loading]", Toast.LENGTH_LONG).show();
	}
	protected void onPostExecute(String time) {
		if(time==null)
			time = "[unknown]";
		Toast.makeText(context, "Next Bus Arrives: " + time, Toast.LENGTH_LONG).show();
	}

}
