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

    /**
     * Getter pour mCalendrier
     *
     * @return un objet GregorianCalendar
     */
    public GregorianCalendar getCalendrier() {
        return mCalendrier;
    }

    public void setCalendrier(GregorianCalendar mCalendrier) {
        this.mCalendrier = mCalendrier;
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
     * Setter pour mHeure
     *
     * @param mHeure l'heure actuelle entre 0 et 23 inclus
     */
    public void setHeure(int mHeure) {
        this.mHeure = mHeure;
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
     * Setter pour mMinute
     *
     * @param mMinute la minute actuelle entre 0 et 59 inclus
     */
    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
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
     * Setter pour mConversion
     *
     * @param mConversion l'heure actuelle au format 'XXX' dans [0,143]
     */
    public void setConversion(String mConversion) {
        this.mConversion = mConversion;
    }

    /**
     * Convertit une paire heure-minute vers le format 'XXX' (dans [0,143])
     *
     * @param heures  l'heure considérée
     * @param minutes la minute considérée
     * @return l'heure au format 'XXX' (dans [0,143])
     */
    public String conversion(int heures, int minutes) {
        String temp = Integer.toString((heures % 12) * 12 + minutes / 5);
        switch (temp.length()) {
            case 1:
                temp = "00" + temp;
                break;
            case 2:
                temp = "0" + temp;
                break;
        }
        return temp;
    }

    /**
     * Met à jour les attributs de l'objet OrionTime
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void miseAJour() {
        setCalendrier(new GregorianCalendar());
        setHeure(getCalendrier().get(Calendar.HOUR));
        setMinute(getCalendrier().get(Calendar.MINUTE));
        setConversion(conversion(getHeure(), getMinute()));
        Log.d(TAG_TIME, "Nouvelle heure : " + getConversion());
    }

}
