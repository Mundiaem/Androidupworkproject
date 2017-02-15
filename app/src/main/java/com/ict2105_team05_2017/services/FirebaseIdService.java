package com.ict2105_team05_2017.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Macharia on 2/15/2017.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseIdService.class.getName();

    public FirebaseIdService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "This is the Token: " + refreshToken);
        sendRegistrationToken(refreshToken);

    }

    private void sendRegistrationToken(String refreshToken) {
    }
}
