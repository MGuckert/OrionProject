package com.example.tactigant20.model;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * Classe qui représente l'heure à la fois au format heure-minute et au format arrondi 5 minutes ([0,143])
 *
 * @author Thibaud P., Roman T.
 * @since 1.1
 */
public class OrionTime {

    private static final String TAG_TIME = "debug_time";

    private GregorianCalendar mCalendrier;
    private int mHeure;
    private int mMinute;
    private String mConversion;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public OrionTime() {
        mCalendrier = new GregorianCalendar();
        miseAJour();
    }

    public void setCalendrier(GregorianCalendar mCalendrier) {
        this.mCalendrier = mCalendrier;
    }

    /**
     * Setter pour mHeure
     *
     * @param mHeure l'heure actuelle entre 0 et 23 inclus
     */
    public void setHeure(int mHeure) {
        this.mHeure = mHeure;
    }

    /**
     * Setter pour mMinute
     *
     * @param mMinute la minute actuelle entre 0 et 59 inclus
     */
    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    /**
     * Setter pour mConversion
     *
     * @param mConversion l'heure actuelle au format 'XXX' dans [0,143]
     */
    public void setConversion(String mConversion) {
        this.mConversion = mConversion;
    }

    /**
     * Getter pour mCalendrier
     *
     * @return un objet GregorianCalendar
     */
    public GregorianCalendar getCalendrier() {
        return mCalendrier;
    }

    /**
     * Getter pour mHeure
     *
     * @return l'heure actuelle entre 0 et 23 inclus
     */
    public int getHeure() {
        return mHeure;
    }

    /**
     * Getter pour mMinute
     *
     * @return la minute actuelle entre 0 et 59 inclus
     */
    public int getMinute() {
        return mMinute;
    }

    /**
     * Getter pour mConversion
     *
     * @return l'heure actuelle au format 'XXX' dans [0,143]
     */
    public String getConversion() {
        return mConversion;
    }

    /**
     * Met à jour les attributs de l'objet OrionTime
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void miseAJour() {
        setCalendrier(new GregorianCalendar());
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
