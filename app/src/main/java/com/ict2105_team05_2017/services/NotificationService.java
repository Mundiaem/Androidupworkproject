package com.ict2105_team05_2017.services;


import android.app.Notification;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.activities.MainActivity;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = NotificationService.class.getName();

    /*
    * onMessageReceived: fro handling messages once they are received by the device*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /*
        * If the application is in the foreground the data is handled from here
        * */
        Log.e(TAG, " This is the message: " + remoteMessage.getData());
        String msg = remoteMessage.getNotification().getBody();
        Log.e(TAG, " This is the Notification: " + msg);
        Log.e(TAG, " This is the Data: " + remoteMessage.getData());

        fireNotification(msg);

    }

    private void fireNotification(String msg) {

        PugNotification.with(this)
                .load()
                .title("Testing")
                .message(msg)

                .bigTextStyle("You have a new Message")
                .smallIcon(R.drawable.ic_drop)
                .largeIcon(R.drawable.ic_drop)
                .sound(Uri.EMPTY)
                .flags(Notification.DEFAULT_ALL)
                .autoCancel(true)
                .click(MainActivity.class)
                .simple()
                .build();
    }
}
