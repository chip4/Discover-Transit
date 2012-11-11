package com.discovertransit;

import java.io.IOException;

import android.database.SQLException;
import android.os.AsyncTask;

public class ChangeDatabaseTask extends AsyncTask<Void,Void,DataBaseHelper> {
	private DataBaseHelper originalDbHelper;
	private MyMapView mapView;
	private String city;
	public ChangeDatabaseTask(MyMapView mapView,String city) {
		this.originalDbHelper = mapView.getDbHelper();
		this.mapView = mapView;
		this.city = city;
	}
	@Override
	protected void onPreExecute() {
		mapView.disableListener();
	}
	@Override
	protected DataBaseHelper doInBackground(Void... arg0) {
		if(city==null) {
			city = "Atlanta";
		}
		if(originalDbHelper!=null) {
			originalDbHelper.close();
			originalDbHelper = null;
		}

		DataBaseHelper dbHelper = new DataBaseHelper(mapView.getContext(),city);
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			dbHelper.openDataBase();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return dbHelper;
	}
	
	@Override
	protected void onPostExecute(DataBaseHelper dbHelper)  {
		mapView.enableListener();
		mapView.setDbHelper(dbHelper);
	}

}
