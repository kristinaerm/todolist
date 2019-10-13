package com.example.todolist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    static private final String CHANNEL1ID = "ChannelTaskID";
    static private final String CHANNEL1NAME = "Tasks";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel channelTask = new NotificationChannel(CHANNEL1ID, CHANNEL1NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channelTask.enableLights(true);
        channelTask.enableVibration(true);
        channelTask.setLightColor(R.color.colorPrimary);
        channelTask.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(channelTask);
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;

    }

    public NotificationCompat.Builder getChannelTaskNotification(String title, String text) {
        Intent mainIntent = new Intent(this, EmailPassowordActivity.class);
        PendingIntent pendingMainIntent = PendingIntent.getActivity(this, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL1ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.label)
                .setAutoCancel(true)
                .setContentIntent(pendingMainIntent);
    }
}
