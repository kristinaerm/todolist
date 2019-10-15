package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    public static final String TITLE = "com.example.todolist.AlertReceiver.Title";
    public static final String TEXT = "com.example.todolist.AlertReceiver.Text";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(TITLE);
        String text = intent.getStringExtra(TEXT);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelTaskNotification(title, text);
        notificationHelper.getNotificationManager().notify(MainAndNavigation.getCountForNotifications(), nb.build());
        MainAndNavigation.IncrementCountForNotifications();
    }
}
