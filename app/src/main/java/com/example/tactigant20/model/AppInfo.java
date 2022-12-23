package com.example.tactigant20.model;

import android.content.pm.ApplicationInfo;

/**
 * Classe qui représente une application, ses informations, son nom et son mode de vibration associé.
 *
 * @author Mathis G.
 * @since 1.0
 */
public class AppInfo {
    private ApplicationInfo mInfo;
    private String mLabel;
    private String mVibrationMode;

    /**
     * Getter pour mInfo
     *
     * @return les informations de l'application
     */
    public ApplicationInfo getInfo() {
        return this.mInfo;
    }

    /**
     * Setter pour mInfo
     *
     * @param mInfo les informations de l'application
     */
    public void setInfo(ApplicationInfo mInfo) {
        this.mInfo = mInfo;
    }

    /**
     * Getter pour mLabel
     *
     * @return le nom de l'application
     */
    public String getLabel() {
        return this.mLabel;
    }

    /**
     * Setter pour mLabel
     *
     * @param mLabel le nom de l'application
     */
    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    /**
     * Getter pour mVibrationMode
     *
     * @return le mode de vibration associé à l'application
     */
    public String getVibrationMode() {
        return this.mVibrationMode;
    }

    /**
     * Setter pour mVibrationMode
     *
     * @param mVibrationMode le mode de vibration qu'on souhaite associer à l'application
     */
    public void setVibrationMode(String mVibrationMode) {
        this.mVibrationMode = mVibrationMode;
    }
}
