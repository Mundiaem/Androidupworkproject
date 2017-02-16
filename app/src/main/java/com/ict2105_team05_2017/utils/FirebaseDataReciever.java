package com.ict2105_team05_2017.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Macharia on 2/16/2017.
 */

public class FirebaseDataReciever extends WakefulBroadcastReceiver {
    private static final  String TAG=FirebaseDataReciever.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "I'm in!!!");

        Bundle dataBundle = intent.getBundleExtra("data");
        Log.d(TAG, dataBundle.toString());

    }
}
