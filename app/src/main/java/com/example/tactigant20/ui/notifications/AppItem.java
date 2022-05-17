package com.example.tactigant20.ui.notifications;

public class AppItem {

    private String name;
    private String mnemonic;
    private int vibrationMode;

    public AppItem(String name, String mnemonic, int vibrationMode) {
        this.name = name;
        this.mnemonic = mnemonic;
        this.vibrationMode = vibrationMode;
    }

    public String getName() {
        return name;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getVibrationMode() {
        return vibrationMode;
    }

}
