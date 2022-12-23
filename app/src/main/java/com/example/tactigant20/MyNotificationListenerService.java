package com.example.tactigant20;

import android.app.Notification;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import com.example.tactigant20.model.BluetoothLowEnergyTool;

//pour que cela fonctionne correctement il semble qu'il faille autoriser l'application à obtenir toutes les notifications
//pour cela taper dans la barre de recherche des paramètres android "accéder aux notifications"
public class MyNotificationListenerService extends NotificationListenerService {

    private final static String TAG_MNLS = "debug_mnls";
    private static String mVibrationModeId;
    private Context mContext;

    public static String getVibrationModeId() {
        return mVibrationModeId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (MainActivity.getMyBLET() != null) {
            if (MainActivity.getMyBLET().getValeurDeConnexion() == BluetoothLowEnergyTool.ValeurDeConnexion.CONNECTE) {
                super.onNotificationPosted(sbn);
                String packageName = sbn.getPackageName();
                Notification notif = sbn.getNotification();
                String category = notif.category;
                mVibrationModeId = MainActivity.getMyVibrationsTool().loadAppVibrationModeId(packageName, getApplicationContext());
                BluetoothGatt gatt = MainActivity.getMyBLET().getGatt();
                MainActivity.getMyBLET().setMode("Ecriture");
                if (category != null) {
                    if (!category.equals("sys")) {
                        showToast("Notification reçue : " + packageName + " Vibration mode : " + mVibrationModeId);
                        try {
                            gatt.discoverServices();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            /*
            Log.i(TAG_MNLS, "Notification postée");
            Log.i(TAG_MNLS, "package name : " + packageName);
            Log.i(TAG_MNLS, "notification : " + notif);
            Log.i(TAG_MNLS, "category : " + category);
             */
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}