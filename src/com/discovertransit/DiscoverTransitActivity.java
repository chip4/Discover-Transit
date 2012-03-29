package com.discovertransit;

import android.os.Bundle;
import org.apache.cordova.*;

public class DiscoverTransitActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}