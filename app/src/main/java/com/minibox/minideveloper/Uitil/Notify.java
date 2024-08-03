package com.minibox.minideveloper.Uitil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class Notify {
    public static void createNotify(Context context,String chanId,String chanName,int importance){
        NotificationManager notifyMag = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notifyMag.getNotificationChannel(chanId) == null){
                NotificationChannel channel = new NotificationChannel(chanId,chanName,importance);
                channel.enableVibration(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setImportance(importance);
                notifyMag.createNotificationChannel(channel);
            }
        }
    }
}
