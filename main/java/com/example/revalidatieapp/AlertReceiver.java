package com.example.revalidatieapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: begint");
            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification();


            int id = intent.getExtras().getInt("id");
            Log.d(TAG, "onReceive: id = " + id);
            notificationHelper.getManager().notify(id, nb.build());
        }
}
