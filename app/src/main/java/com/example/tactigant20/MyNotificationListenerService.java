package com.example.tactigant20;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

//pour que cela fonctionne correctement il semble qu'il faille autoriser l'application à obtenir toutes les notifications
//pour cela taper dans la barre de recherche des paramètres android "accéder aux notifications"
public class MyNotificationListenerService extends NotificationListenerService {
    private Context context;
    private final static String TAG = "NotificationInfo";
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();
        Notification notif = sbn.getNotification();
        String category = notif.category;
        Log.d(TAG, "notificationPosted");
        Log.i(TAG, "package name : "+packageName);
        Log.i(TAG,"notification : "+notif);
        Log.i(TAG,"category : "+category);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}