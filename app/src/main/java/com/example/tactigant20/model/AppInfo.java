package com.example.tactigant20.model;

import android.content.pm.ApplicationInfo;

//Objet application (avec ses informations, son nom, et son mode de vibration)
public class AppInfo {
    private ApplicationInfo mInfo;
    private String mLabel;
    private String mVibrationMode;

    public ApplicationInfo getInfo() {
        return this.mInfo;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getVibrationMode() {
        return this.mVibrationMode;
    }

    public void setInfo(ApplicationInfo mInfo) {
        this.mInfo = mInfo;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public void setVibrationMode(String mVibrationMode) {
        this.mVibrationMode = mVibrationMode;
    }
}
