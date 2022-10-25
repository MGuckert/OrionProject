package com.example.tactigant20.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;

import java.lang.ref.WeakReference;

public class NotificationTool{

    private static final String TAG_NT = "debug_NT";

    private static CustomUIThread myNTCustomUIThread;

    private final WeakReference<Context> mContext;
    private final String mID1; // ID du Builder
    private final int mID2; // ID de notification
    private final String mDescription;

    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat myNotificationManager;

    public NotificationTool(Context mContext, String mID1, int mID2, String mDescription) {
        this.mContext = new WeakReference<>(mContext);
        this.mID1 = mID1;
        this.mID2 = mID2;
        this.mDescription = mDescription;
    }

    public void createNotificationChannel(String text) {
        // Paramètres de la notification
        mBuilder = new NotificationCompat.Builder(this.mContext.get(), this.mID1)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Déconnecté")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Fait apparaitre la notification
        this.myNotificationManager = NotificationManagerCompat.from(this.mContext.get());

        myNotificationManager.notify(this.mID2, mBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // IMPORTANCE_LOW pour ne pas avoir de son
            NotificationChannel channel = new NotificationChannel(this.mID1, this.mDescription, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(this.mDescription);
            this.mContext.get().getSystemService(NotificationManager.class).createNotificationChannel(channel);

            myNTCustomUIThread = new CustomUIThread();
            myNTCustomUIThread.start();
        } else {
            Log.w(TAG_NT, "Pas de bandeau de notification pour < Android O");
        }
    }

    public void clearNotification() {
        ((NotificationManager) this.mContext.get().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(this.mID2);
    }

    @SuppressWarnings({"BusyWait"})
    public class CustomUIThread extends Thread {

        private boolean running = false;

        @Override
        public void run() {
            Log.d(TAG_NT, "Lancement du thread");
            this.running = true;
            while (running) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.getMyBLET() != null) {
                    switch (MainActivity.getMyBLET().getValeurDeConnexion()) {
                        case DECONNECTE:
                            mBuilder.setContentTitle("Déconnecté");
                            break;
                        case CHARGEMENT:
                            mBuilder.setContentTitle("Chargement");
                            break;
                        case CONNECTE:
                            mBuilder.setContentTitle("Connecté");
                            break;
                    }
                    myNotificationManager.notify(mID2, mBuilder.build());
                }
            }
        }
        public void setRunning(Boolean running) {
            this.running = running;
        }
    }

    public static CustomUIThread getCustomUIThread() {
        return myNTCustomUIThread;
    }

}