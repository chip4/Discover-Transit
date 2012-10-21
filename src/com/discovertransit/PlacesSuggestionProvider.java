package com.discovertransit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class PlacesSuggestionProvider extends ContentProvider {
	private static final String LOG_TAG = "com.discovertransit";

	public static final String AUTHORITY = "com.discovertransit.search_suggestion_provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/search");


	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyCRoi556ysGOx6IrGeAzS7YtdGVHVKvV7E";

	// UriMatcher constant for search suggestions
	private static final int SEARCH_SUGGEST = 3;

	private static final UriMatcher uriMatcher;

	private static final String[] SEARCH_SUGGEST_COLUMNS = {
		BaseColumns._ID,
		SearchManager.SUGGEST_COLUMN_TEXT_1,
		//SearchManager.SUGGEST_COLUMN_TEXT_2,
		SearchManager.SUGGEST_COLUMN_INTENT_DATA
	};

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
	}

	private LocationManager myLocationManager;
	private String bestprovider;
	private Context context;

	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		context = this.getContext();
		myLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestprovider = myLocationManager.getBestProvider(criteria, false);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Log.d(LOG_TAG, "query = " + uri);

		Location location = myLocationManager.getLastKnownLocation(bestprovider);

		Log.d(LOG_TAG, "Lat: "+location.getLatitude()+" Lon: "+location.getLongitude());

		if(uriMatcher.match(uri)==SEARCH_SUGGEST) {
			Log.d(LOG_TAG, "Search suggestions requested.");
			return getResults(uri.getLastPathSegment().toLowerCase(),location.getLatitude(),location.getLongitude());
		}
		else {
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private MatrixCursor getResults(String query,double latitude, double longitude) {
		MatrixCursor results = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=true&key=" + API_KEY);
			sb.append("&radius=5000");
			sb.append("&location="+latitude+","+longitude);
			sb.append("&input=" + URLEncoder.encode(query, "utf8"));
			
			Log.d(LOG_TAG, "url = " + sb.toString());

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			
			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return results;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return results;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObject = new JSONObject(jsonResults.toString());
			JSONArray predictions = jsonObject.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			results = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 1);
			for (int i = 0; i < predictions.length(); i++) {
				jsonObject = predictions.getJSONObject(i);
				results.addRow(new String[] {""+(i+2),jsonObject.getString("description"),jsonObject.getString("reference")});
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return results;
	}

	@Override
	public int update(Uri uri, ContentValues arg1, String arg2, String[] arg3) {
		throw new UnsupportedOperationException();
	}
}