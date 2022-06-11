package com.example.tactigant20.ui.notifications;

import android.content.pm.ApplicationInfo;

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
