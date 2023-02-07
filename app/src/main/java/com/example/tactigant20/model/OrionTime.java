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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void miseAJour() {
        setHeure(calendrier.get(Calendar.HOUR));
        setMinute(calendrier.get(Calendar.MINUTE));
        setConversion(Integer.toString((this.heure%12)*12 + this.minute/5));
        switch(this.conversion.length()) {
            case 1:
                this.conversion = "00" + this.conversion;
                break;
            case 2:
                this.conversion = "0" + this.conversion;
                break;
        }
        Log.d(TAG_TIME, "Nouvelle heure : " + conversion);
    }

}
