package com.example.ucochat.Notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class Oreo extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.ucochat";
    private static final String CHANNEL_NAME = "ucochat";

    private NotificationManager manager;

    public Oreo(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);

    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNot (String title, String body, PendingIntent intent, Uri sound, String icon) {

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID).setContentTitle(title).setContentText(body)
            .setContentIntent(intent).setSound(sound).setSmallIcon(Integer.parseInt(icon)).setAutoCancel(true);
    }



}
