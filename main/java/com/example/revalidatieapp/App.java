package com.example.revalidatieapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String TIME_TO_PRACTICE = "it is time to practice";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    TIME_TO_PRACTICE,
                    "time to practice",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Het is tijd om te oefenen.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
