package com.ict2105_team05_2017.app;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by Macharia on 2/11/2017.
 */

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.setApplicationId("1215372291832513");
        FacebookSdk.sdkInitialize(this);
    }
}
