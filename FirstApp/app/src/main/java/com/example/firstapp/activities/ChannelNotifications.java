package com.example.firstapp.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;

import java.nio.channels.Channel;

public class ChannelNotifications extends Application {
    public static final String CHANNEL_1 = "channel1";
    public static final String CHANNEL_2 = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            System.out.println("here!");
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is channel1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("This is channel2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }

    }
}
