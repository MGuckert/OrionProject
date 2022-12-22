package com.example.tactigant20.model;

import android.content.pm.ApplicationInfo;

/**
 * Classe qui représente une application, ses informations, son nom et son mode de vibration associé.
 */
public class AppInfo {
    private ApplicationInfo mInfo;
    private String mLabel;
    private String mVibrationMode;

    public ApplicationInfo getInfo() {
        return this.mInfo;
    }

    public void setInfo(ApplicationInfo mInfo) {
        this.mInfo = mInfo;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public String getVibrationMode() {
        return this.mVibrationMode;
    }

    public void setVibrationMode(String mVibrationMode) {
        this.mVibrationMode = mVibrationMode;
    }
}
