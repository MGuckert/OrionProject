package com.example.tactigant20;

import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tactigant20.ui.notifications.NotificationsFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

//pour que cela fonctionne correctement il semble qu'il faille autoriser l'application à obtenir toutes les notifications
//pour cela taper dans la barre de recherche des paramètres android "accéder aux notifications"
public class MyNotificationListenerService extends NotificationListenerService {
    private Context context;
    private final static String TAG_MNLS = "NotificationInfo";
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
        String vibrationMode = NotificationsFragment.loadVibrationMode(packageName, getApplicationContext());
        if(category != null)
            if (!category.equals("sys"))//attention risque de NullPointerException !!!
                showToast("Notification reçu : "+packageName + " Vibration mode : " + vibrationMode);
        Log.d(TAG_MNLS, "notificationPosted");
        Log.i(TAG_MNLS, "package name : "+packageName);
        Log.i(TAG_MNLS,"notification : "+notif);
        Log.i(TAG_MNLS,"category : "+category);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    public void showToast(String msg){Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();}

    //l'implémentation fournie par NotificationsFragment n'est pas utilisable car la méthode est privée et pas statique
    //La méthode à également besoin du contexte
    /*private String loadVibrationMode(String notifName) {
        //Fonction renvoyant le mode de vibration de l'application qui a pour package "notifName" sauvegardé dans le fichier
        // "vibration_modes_data.txt", et "UNKNOWN" si aucune donnée pour cette application n'a été sauvegardée.
        FileInputStream inputStream = null;
        try {
            inputStream = getApplicationContext().openFileInput("vibration_modes_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(inputReader);

            String line = null;
            do { //On lit le fichier ligne par ligne, en comparant le début de chaque ligne avec "notifName" :
                // s'il est identique, on est sur la bonne ligne, et on peut renvoyer le mode de vibration écrit !
                try {
                    line = buffReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.err.println(line);
                int n = notifName.length();
                if ( line != null && n < line.length()) {
                    if (line.substring(0, n).equals(notifName)) {
                        return line.substring(line.length()-1, line.length());
                    }
                }
            } while (line != null);
        }
        return "UNKNOWN";
    }*/
}