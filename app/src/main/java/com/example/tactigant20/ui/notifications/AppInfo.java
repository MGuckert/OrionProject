package com.example.tactigant20.ui.notifications;

import android.content.pm.ApplicationInfo;

//Objet application (avec ses informations, son nom, et son mode de vibration)
public class AppInfo {
    public ApplicationInfo info;
    public String label;
    public String vibrationMode;

    public ApplicationInfo getInfo() {
        return info;
    }

    public String getLabel() {
        return label;
    }

    public String getVibrationMode() {
        return vibrationMode;
    }

    public void setVibrationMode(String vibrationMode) {
        this.vibrationMode = vibrationMode;
    }
}
