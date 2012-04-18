package com.discovertransit;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
 
public class MyMapView extends MapView
{
    // ------------------------------------------------------------------------
    // LISTENER DEFINITIONS
    // ------------------------------------------------------------------------
 
    // Change listener
    public interface OnChangeListener
    {
        public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom);
    }
 
    // ------------------------------------------------------------------------
    // MEMBERS
    // ------------------------------------------------------------------------
 
    private MyMapView mThis;
    private long mEventsTimeout = 250L;     // Set this variable to your preferred timeout
    private boolean mIsTouched = false;
    private GeoPoint mLastCenterPosition;
    private int mLastZoomLevel;
    private Timer mChangeDelayTimer = new Timer();
    private MyMapView.OnChangeListener mChangeListener = null;
    private boolean isRouteDisplayed = false;
    private DataBaseHelper dbHelper;
	private boolean refresh = false;
	private ArrayList<Drawable> drawableList;
 
    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------
 
    public DataBaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DataBaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public boolean isRouteDisplayed() {
		return isRouteDisplayed;
	}

	public void setRouteDisplayed(boolean isRouteDisplayed) {
		this.isRouteDisplayed = isRouteDisplayed;
	}

	public MyMapView(Context context, String apiKey)
    {
        super(context, apiKey);
        init();
    }
 
    public MyMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
 
    public MyMapView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
 
    private void init()
    {
        mThis = this;
        mLastCenterPosition = this.getMapCenter();
        mLastZoomLevel = this.getZoomLevel();
    }

 
    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
 
    public void setOnChangeListener(MyMapView.OnChangeListener l)
    {
        mChangeListener = l;
    }
 
    // ------------------------------------------------------------------------
    // EVENT HANDLERS
    // ------------------------------------------------------------------------
 
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        // Set touch internal
        mIsTouched = (ev.getAction() != MotionEvent.ACTION_UP);
 
        return super.onTouchEvent(ev);
    }
 
    @Override
    public void computeScroll()
    {
        super.computeScroll();
 
        // Check for change
        if (isSpanChange() || isZoomChange())
        {
            // If computeScroll called before timer counts down we should drop it and
            // start counter over again
            resetMapChangeTimer();
        }
    }
 
    // ------------------------------------------------------------------------
    // TIMER RESETS
    // ------------------------------------------------------------------------
 
    private void resetMapChangeTimer()
    {
        mChangeDelayTimer.cancel();
        mChangeDelayTimer = new Timer();
        mChangeDelayTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (mChangeListener != null) mChangeListener.onChange(mThis, getMapCenter(), mLastCenterPosition, getZoomLevel(), mLastZoomLevel);
                mLastCenterPosition = getMapCenter();
                mLastZoomLevel = getZoomLevel();
            }
        }, mEventsTimeout);
    }
 
    // ------------------------------------------------------------------------
    // CHANGE FUNCTIONS
    // ------------------------------------------------------------------------
 
    private boolean isSpanChange()
    {
        return !mIsTouched && !getMapCenter().equals(mLastCenterPosition);
    }
 
    private boolean isZoomChange()
    {
        return (getZoomLevel() != mLastZoomLevel);
    }

	public boolean forceRefresh() {
		return refresh ;
	}
	
	public void setForceRefresh(boolean refresh) {
		this.refresh = refresh;
	}
	
	public List<Drawable> getDrawableList() {
		drawableList = new ArrayList<Drawable>();
		drawableList.add(this.getResources().getDrawable(R.drawable.m1));
		drawableList.add(this.getResources().getDrawable(R.drawable.m2));
		drawableList.add(this.getResources().getDrawable(R.drawable.m3));
		drawableList.add(this.getResources().getDrawable(R.drawable.m4));
		drawableList.add(this.getResources().getDrawable(R.drawable.m5));
		drawableList.add(this.getResources().getDrawable(R.drawable.m6));
		drawableList.add(this.getResources().getDrawable(R.drawable.m7));
		drawableList.add(this.getResources().getDrawable(R.drawable.m8));
		drawableList.add(this.getResources().getDrawable(R.drawable.m9));
		drawableList.add(this.getResources().getDrawable(R.drawable.m10));
		return drawableList;
	}
 
}