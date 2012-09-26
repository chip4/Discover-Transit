package com.discovertransit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;

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

		if((myDataBase != null) && myDataBase.isOpen())
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
	public Map<Integer, ItemizedOverlayActivity> getStopsNearby(double minLat, double minLon, double maxLat, double maxLon,boolean limit, List<Drawable> draw, MyMapView mapView) {
		String amount = "";
		if(limit)
			amount = "AND amount<2";
		String query = "SELECT _id,stop,direction,lat,lon,route,amount FROM BusStops WHERE (lat BETWEEN '"+minLat+"' AND '"+maxLat+
				"' AND lon BETWEEN '"+minLon+"' AND '"+maxLon+"'"+amount+") ORDER BY route";
		System.out.println(query);
		Cursor cursor = myDataBase.rawQuery(query,null);
		
		Map<Integer, ItemizedOverlayActivity> mMap = new HashMap<Integer, ItemizedOverlayActivity>();
		if(!cursor.moveToFirst()) return null;
		int route = cursor.getInt(5);
		ItemizedOverlayActivity curItemizedOverlay = new ItemizedOverlayActivity(draw.get(route%10),mapView,route);
		while(!cursor.isAfterLast()) {
			GeoPoint point = new GeoPoint((int)(cursor.getDouble(3)*1E6),(int)(cursor.getDouble(4)*1E6));
			route = cursor.getInt(5);
			String stopName = cursor.getString(1);
			String dir = cursor.getString(2);
			if(!mMap.containsKey(route))
			{
				/*if(curItemizedOverlay!=null) {
					curItemizedOverlay.callPopulate();
				}*/
				curItemizedOverlay = new ItemizedOverlayActivity(draw.get(route%10),mapView,route);
				mMap.put(route, curItemizedOverlay);

			}
			MyOverlayItem stopOverlayItem = new MyOverlayItem(new Stop(point,"Route "+route+": "+dir,stopName,route,stopName,dir));
			curItemizedOverlay.addOverlay(stopOverlayItem);
			cursor.moveToNext();
		}
		return mMap;
	}
	
	
	public Map<Integer, ItemizedOverlayActivity> addOverlappingStopsNearby(double minLat, double minLon, double maxLat, double maxLon,List<Drawable> draw, MyMapView mapView,Map<Integer, ItemizedOverlayActivity> mMap) {
		String query = "SELECT _id, lat,lon,amount,stop,direction,route FROM BusStops WHERE (lat BETWEEN '"+
				+minLat+"' AND '"+maxLat+"' AND lon BETWEEN '"+minLon+"' AND '"+maxLon+"' AND amount>1) ORDER BY lat,lon";
		System.out.println(query);
		Cursor cursor = myDataBase.rawQuery(query,null);
		if(!cursor.moveToFirst()) return null;
		if(mMap == null) mMap = new HashMap<Integer, ItemizedOverlayActivity>();
		double total = cursor.getDouble(3);
		double cur = cursor.getDouble(3);
		double delta = 0;
		while(!cursor.isAfterLast()) {
			if(cur==0) {
				delta = 0;
				cur = cursor.getDouble(3);
				total= cur;
			}
			int curRoute = cursor.getInt(6);
			String stopName = cursor.getString(4);
			String dir = cursor.getString(5);
			GeoPoint point = pointNearby(cursor.getDouble(1),cursor.getDouble(2),7,delta);
			if(!mMap.containsKey(curRoute)) {
				mMap.put(curRoute, new ItemizedOverlayActivity(draw.get(curRoute%10),mapView,curRoute));
			}
			MyOverlayItem stopOverlayItem = new MyOverlayItem(new Stop(point,"Route "+curRoute+": "+dir,stopName,curRoute,stopName,dir));
			mMap.get(curRoute).addOverlay(stopOverlayItem);
			delta+=Math.PI*2/total;
			cur--;
			cursor.moveToNext();
		}
		return mMap;
	}
	
	public GeoPoint pointNearby(double lat, double lon,double distance,double delta) {

		lat = Math.toRadians(lat);
		lon = Math.toRadians(lon);
		double dist = Math.toRadians(distance / 1852d / 60d);
		double lat1 = Math.asin(Math.sin(lat) * Math.cos(dist) + Math.cos(lat)*Math.sin(dist)*Math.cos(delta*2*Math.PI));
		double lon1 = (lon + Math.atan2(Math.sin(delta*2*Math.PI) * Math.sin(dist) * Math.cos(lat), Math.cos(dist) - Math.sin(lat) * Math.sin(lat1))+ Math.PI) % (2 * Math.PI) - Math.PI;
		return new GeoPoint((int)(Math.toDegrees(lat1)*1E6),(int)(Math.toDegrees(lon1)*1E6));
	}
	
	public int getStopsforRoute(int route, Drawable draw, MyMapView mapView,ItemizedOverlayActivity curItemizedOverlay) {
		Cursor cursor = myDataBase.rawQuery("SELECT _id,stop,direction,lat,lon,route FROM BusStops where(route='"+route+"') ORDER BY route",null);
		//GeoPoint point;
		if(!cursor.moveToFirst()) return -1;
		int size = 0;
		while(!cursor.isAfterLast()) {
			GeoPoint point = new GeoPoint((int)(cursor.getDouble(3)*1E6),(int)(cursor.getDouble(4)*1E6));
			String stopName = cursor.getString(1);
			String dir = cursor.getString(2);
			MyOverlayItem stopOverlayItem = new MyOverlayItem(new Stop(point,"Route "+route+": "+dir,stopName,route,stopName,dir));
			curItemizedOverlay.addOverlay(stopOverlayItem);
			cursor.moveToNext();
		}
		return size;
	}


}