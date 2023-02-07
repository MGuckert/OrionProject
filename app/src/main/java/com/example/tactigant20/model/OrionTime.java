package com.example.tactigant20.model;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class OrionTime {

    private static final String TAG_TIME = "debug_time";

    private final GregorianCalendar calendrier;
    private int heure;
    private int minute;
    private String conversion;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public OrionTime() {
        calendrier = new GregorianCalendar();
        miseAJour();
        }

    public void setHeure(int heure) {
        this.heure = heure;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    public GregorianCalendar getCalendrier() {
        return calendrier;
    }

    public int getHeure() {
        return heure;
    }

    public int getMinute() {
        return minute;
    }

    public String getConversion() {
        return conversion;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void miseAJour() {
        setHeure(getCalendrier().get(Calendar.HOUR));
        setMinute(getCalendrier().get(Calendar.MINUTE));
        setConversion(Integer.toString((getHeure()%12)*12 + getMinute()/5));
        switch(getConversion().length()) {
            case 1:
                setConversion("00" + getConversion());
                break;
            case 2:
                setConversion("0" + getConversion());
                break;
        }
        Log.d(TAG_TIME, "Nouvelle heure : " + getConversion());
    }

}
