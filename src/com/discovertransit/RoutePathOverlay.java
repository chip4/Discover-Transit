package com.discovertransit;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Pair;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
 

//From 
//   http://djsolid.net/blog/android---draw-a-path-array-of-points-in-mapview
public class RoutePathOverlay extends Overlay {
 
        private int _pathColor;
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
 
        public RoutePathOverlay(ArrayList<ArrayList<GeoPoint>> points) {
        		_points=null;
                pathCoords = (ArrayList<ArrayList<GeoPoint>>)points.clone();
                _pathColor = Color.RED;
                _drawStartEnd = false;
        }
 
        private void drawOval(Canvas canvas, Paint paint, Point point) {
                Paint ovalPaint = new Paint(paint);
                ovalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                ovalPaint.setStrokeWidth(2);
                int _radius = 6;
                RectF oval = new RectF(point.x - _radius, point.y - _radius, point.x + _radius, point.y + _radius);
                canvas.drawOval(oval, ovalPaint);               
        }
 
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
                Projection projection = mapView.getProjection();
                //if (shadow == false && _points != null) {
                	for(ArrayList<GeoPoint> pair : pathCoords){
                        Path path = new Path();
                        //We are creating the path
                        for (int i = 0; i < 2; i++) {
                                GeoPoint gPointA = pair.get(i);
                                Point pointA = new Point();
                                pointA = projection.toPixels(gPointA, pointA);
                                if (i == 0) { //This is the start point
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
                        if (!path.isEmpty())
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
}