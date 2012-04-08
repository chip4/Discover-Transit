package com.discovertransit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

public class DataBaseHelper extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.discovertransit/databases/";

	private static String DB_NAME = "TransitStops";

	private SQLiteDatabase myDataBase; 

	private final Context myContext;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		boolean dbExist = false;//checkDataBase();

		if(dbExist){
			//do nothing - database already exist
		}else{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			this.close();

			try {
				this.close();
				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{

		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException{

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.
	public int getStopsNearby(double minLat, double minLon, double maxLat, double maxLon, List<Drawable> draw, MyMapView mapView,List<ItemizedOverlayActivity> itemizedOverlayList) throws JSONException {
		System.out.println("SELECT _id,stop,direction,lat,lon,route FROM Stops where(lat<'"+minLat+"' AND lon<'"+minLon+"' AND lat>'"+maxLat+"' AND lon>'"+maxLon+"') ORDER BY route");
		Cursor cursor = myDataBase.rawQuery("SELECT _id,stop,direction,lat,lon,route FROM Stops where(lat<'"+minLat+"' AND lon<'"+minLon+"' AND lat>'"+maxLat+"' AND lon>'"+maxLon+"') ORDER BY route",null);
		//GeoPoint point;
		if(!cursor.moveToFirst()) return -1;
		int size = 0;
		int curRoute = cursor.getInt(5);
		int drawableIndex = draw.size()-1;
		ItemizedOverlayActivity curItemizedOverlay = new ItemizedOverlayActivity(draw.get(drawableIndex),mapView,curRoute);
		while(!cursor.isAfterLast()) {
			GeoPoint point = new GeoPoint((int)(cursor.getDouble(3)*1E6),(int)(cursor.getDouble(4)*1E6));
			int route = cursor.getInt(5);
			String stopName = cursor.getString(1);
			String dir = cursor.getString(2);
			String time = "";//getTime(route,dir,stopName);
			Stop stop = new Stop(point,stopName,dir+ "--Next bus arrives at: "+time,route);
			if(route!=curRoute)
			{
				curRoute = route;
				drawableIndex--;
				size++;
				if(drawableIndex<0) drawableIndex = draw.size()-1;
				itemizedOverlayList.add(curItemizedOverlay);
				curItemizedOverlay = new ItemizedOverlayActivity(draw.get(drawableIndex),mapView,curRoute);

			}
			curItemizedOverlay.addOverlay(stop.getOverlay());
			cursor.moveToNext();
		}
		size++;
		itemizedOverlayList.add(curItemizedOverlay);
		return size;
	}
	
	public int getStopsforRoute(int route, Drawable draw, MyMapView mapView,List<ItemizedOverlayActivity> itemizedOverlayList) throws JSONException {
		Cursor cursor = myDataBase.rawQuery("SELECT _id,stop,direction,lat,lon,route FROM Stops where(route='"+route+"') ORDER BY route",null);
		//GeoPoint point;
		if(!cursor.moveToFirst()) return -1;
		int size = 0;
		ItemizedOverlayActivity curItemizedOverlay = new ItemizedOverlayActivity(draw,mapView,route);
		while(!cursor.isAfterLast()) {
			GeoPoint point = new GeoPoint((int)(cursor.getDouble(3)*1E6),(int)(cursor.getDouble(4)*1E6));
			String stopName = cursor.getString(1);
			String dir = cursor.getString(2);
			String time = "";//getTime(route,dir,stopName);
			Stop stop = new Stop(point,stopName,dir+ "--Next bus arrives at: "+time,route);
			curItemizedOverlay.addOverlay(stop.getOverlay());
			cursor.moveToNext();
		}
		size++;
		itemizedOverlayList.add(curItemizedOverlay);
		return size;
	}

	public String getTime(int route,String direction,String stopName) throws JSONException {
		String nextTime = "[unknown]";
		JSONArray time;
		stopName = stopName.replace(" ","");
		stopName = stopName.replace(".", "");
		JSONObject json = MapViewActivity.connect("http://discovertransit.herokuapp.com/times/"+route+"/"+stopName+"/"+direction+".json");
		if(json!=null) {
			try {
				json = (JSONObject) json.get("data");
				time = json.getJSONArray("times");
				if(time.length()>0)
					nextTime = time.get(0).toString();
				else
					nextTime = "[unknown]";
			} catch (JSONException e) {
				nextTime ="[unknown]";
			}
		}
		return nextTime;
	}

}