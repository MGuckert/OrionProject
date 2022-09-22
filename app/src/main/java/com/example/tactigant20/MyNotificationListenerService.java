package com.example.tactigant20;

import android.app.Notification;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.example.tactigant20.ui.home.HomeFragment;
import com.example.tactigant20.ui.notifications.NotificationsFragment;

//pour que cela fonctionne correctement il semble qu'il faille autoriser l'application à obtenir toutes les notifications
//pour cela taper dans la barre de recherche des paramètres android "accéder aux notifications"
public class MyNotificationListenerService extends NotificationListenerService {

    private final static String TAG_MNLS = "debug_mnls";

    private final Context context = getApplicationContext();

    public static String vibrationMode;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();
        Notification notif = sbn.getNotification();
        String category = notif.category;
        vibrationMode = NotificationsFragment.loadVibrationMode(packageName, getApplicationContext());
        BluetoothGatt gatt = HomeFragment.getGatt();
        HomeFragment.Mode = "Ecriture";
        if(category != null) {
            if (!category.equals("sys")) { //attention risque de NullPointerException !!!
                showToast("Notification reçue : " + packageName + " Vibration mode : " + vibrationMode);
                try {
                    gatt.discoverServices();
                } catch (SecurityException e) {
                    Log.e(TAG_MNLS, "SecurityError dans MNLS");
                }
            }
        }

        Log.d(TAG_MNLS, "notificationPosted");
        Log.i(TAG_MNLS, "package name : "+packageName);
        Log.i(TAG_MNLS,"notification : "+notif);
        Log.i(TAG_MNLS,"category : "+category);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}