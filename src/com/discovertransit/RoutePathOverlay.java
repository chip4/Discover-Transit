package com.discovertransit;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
 

//From 
//   http://djsolid.net/blog/android---draw-a-path-array-of-points-in-mapview
public class RoutePathOverlay extends Overlay {
 
        private int _pathColor;
        @SuppressWarnings("unused")
		private final List<GeoPoint> _points;
    	private ArrayList<ArrayList<GeoPoint>> pathCoords;
        private boolean _drawStartEnd;
 
        public RoutePathOverlay(List<GeoPoint> points) {
                this(points, Color.RED, true);
        }
 
        public RoutePathOverlay(List<GeoPoint> points, int pathColor, boolean drawStartEnd) {
                _points = points;
                _pathColor = pathColor;
                _drawStartEnd = drawStartEnd;
        }
 
        @SuppressWarnings("unchecked")
		public RoutePathOverlay(ArrayList<ArrayList<GeoPoint>> points) {
        		_points=null;
                pathCoords = (ArrayList<ArrayList<GeoPoint>>)points.clone();
                _pathColor = Color.RED;
                _drawStartEnd = false;
        }
        
        @SuppressWarnings("unchecked")
		public RoutePathOverlay(ArrayList<ArrayList<GeoPoint>> points,int pathColor) {
        		_points=null;
                pathCoords = (ArrayList<ArrayList<GeoPoint>>)points.clone();
                _pathColor = pathColor;
                _drawStartEnd = false;
        }
 
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
                Projection projection = mapView.getProjection();
                //if (shadow == false && _points != null) {
                	for(ArrayList<GeoPoint> pair : pathCoords){
                        Path path = new Path();
                        //We are creating the path
                        GeoPoint gPointA = null;
                        GeoPoint gPointB = null;
                        for (int i = 0; i < 2; i++) {
                                gPointA = pair.get(i);
                                Point pointA = new Point();
                                pointA = projection.toPixels(gPointA, pointA);
                                if (i == 0) { //This is the start point
                                		gPointB = pair.get(i);
                                        path.moveTo(pointA.x, pointA.y);
                                } else {                                    
                                	path.lineTo(pointA.x, pointA.y);
                                }
                        }
 
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setColor(_pathColor);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(5);
                        paint.setAlpha(90);
                        if (!path.isEmpty()&&isCurrentLocationVisible(gPointB,gPointA,mapView))
                                canvas.drawPath(path, paint);
                	}
                //}
                return super.draw(canvas, mapView, shadow, when);
        }
 
        public boolean getDrawStartEnd() {
                return _drawStartEnd;
        }
 
        public void setDrawStartEnd(boolean markStartEnd) {
                _drawStartEnd = markStartEnd;
        }
    	private static boolean isCurrentLocationVisible(GeoPoint start,GeoPoint end,MapView mapView) {
    		if(start==null||end==null) return false;
            Rect currentMapBoundsRect = new Rect();
            Point startPosition = new Point();
            Point endPosition = new Point();

            mapView.getProjection().toPixels(start, startPosition);
            mapView.getProjection().toPixels(end, endPosition);
            mapView.getDrawingRect(currentMapBoundsRect);

            return currentMapBoundsRect.contains(startPosition.x,
                    startPosition.y)||currentMapBoundsRect.contains(endPosition.x,endPosition.y);

        }
}