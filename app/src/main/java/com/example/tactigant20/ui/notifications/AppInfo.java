package com.example.tactigant20.ui.notifications;

import android.content.pm.ApplicationInfo;

//Objet application (avec ses informations, son nom, et son mode de vibration)
public class AppInfo {
    private ApplicationInfo info;
    private String label;
    private String vibrationMode;

    public ApplicationInfo getInfo() {
        return this.info;
    }

    public String getLabel() {
        return this.label;
    }

    public String getVibrationMode() {
        return this.vibrationMode;
    }

    public void setInfo(ApplicationInfo info) {
        this.info = info;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setVibrationMode(String vibrationMode) {
        this.vibrationMode = vibrationMode;
    }
}
