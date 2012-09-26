package com.discovertransit;

import org.json.JSONArray;

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
		String time = null;
		try {
			JSONArray times = APIHelper.getJSONObject(stopURL).getJSONArray("times");
			if(times.length()>0) {
				time = times.get(0).toString();
			}
		} catch (Exception e) {
			Toast.makeText(context, "Unable to retrieve arrival time. Please try again.", Toast.LENGTH_LONG).show();
		}
		return time;
	}
	protected void onPreExecute() {
		Toast.makeText(context, "Retrieving Arrival Time ...", Toast.LENGTH_SHORT).show();
	}
	protected void onPostExecute(String time) {
		if(time==null)
			time = "[unknown]";
		Toast.makeText(context, "Next Bus Arrives: " + time, Toast.LENGTH_LONG).show();
	}

}
