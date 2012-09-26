package com.discovertransit;

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
		if(input==null || input[0]==null)
			return null;
		String stopURL = input[0];
		String time = "[unknown]";
		String output = null;
		try {
			JSONObject stopObject = APIHelper.getJSONObject(stopURL);
			if(!stopObject.has("times"))
				return null;
			JSONArray times = stopObject.getJSONArray("times");
			if(times.length()>0) {
				time = times.get(0).toString();
			}
			output = "Next Bus Arrives: " + time;
		} catch (Exception e) {
			e.printStackTrace();
			output = null;
		}
		return output;
	}
	protected void onPreExecute() {
		Toast.makeText(context, "Retrieving Arrival Time ...", Toast.LENGTH_SHORT).show();
	}
	protected void onPostExecute(String value) {
		if(value==null)
			value = "Unable to retrieve arrival time.";
		Toast.makeText(context, value, Toast.LENGTH_LONG).show();
	}

}
