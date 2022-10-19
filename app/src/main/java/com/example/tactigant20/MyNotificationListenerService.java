package com.example.tactigant20;

import android.app.Notification;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

//pour que cela fonctionne correctement il semble qu'il faille autoriser l'application à obtenir toutes les notifications
//pour cela taper dans la barre de recherche des paramètres android "accéder aux notifications"
public class MyNotificationListenerService extends NotificationListenerService {

    private final static String TAG_MNLS = "debug_mnls";

    private Context mContext;
    private static String mVibrationMode;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (MainActivity.getMyBLET() != null) {
            super.onNotificationPosted(sbn);
            String packageName = sbn.getPackageName();
            Notification notif = sbn.getNotification();
            String category = notif.category;
            mVibrationMode = MainActivity.getMyVibrationsTool().loadVibrationMode(packageName, getApplicationContext());
            BluetoothGatt gatt = MainActivity.getMyBLET().getGatt();
            MainActivity.getMyBLET().setMode("Ecriture");
            if (category != null) {
                if (!category.equals("sys")) { //attention risque de NullPointerException !!!
                    showToast("Notification reçue : " + packageName + " Vibration mode : " + mVibrationMode);
                    try {
                        gatt.discoverServices();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG_MNLS, "notificationPosted");
            Log.i(TAG_MNLS, "package name : " + packageName);
            Log.i(TAG_MNLS, "notification : " + notif);
            Log.i(TAG_MNLS, "category : " + category);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    public static String getVibrationMode() {
        return mVibrationMode;
    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}