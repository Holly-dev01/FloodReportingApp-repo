package com.example.floodreportingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.floodreportingapp.utils.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case Intent.ACTION_BOOT_COMPLETED:
                    // Initialize notification channel after device reboot
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    notificationHelper.createNotificationChannel();
                    break;

                case "com.example.floodreportingapp.REPORT_SUBMITTED":
                    // Handle report submission notification action
                    String message = intent.getStringExtra("message");
                    if (message != null) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }
    }
}