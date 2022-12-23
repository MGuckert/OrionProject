package com.example.tactigant20.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe représentant un mode de vibration, qui est défini par un nom et un identifiant unique.
 * Cette classe permet également de sauvegarder et de récupérer des instances de VibrationMode dans les préférences partagées de l'application.
 */
public class VibrationMode {

    private String name;
    private String id;

    public VibrationMode(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public VibrationMode(JSONObject json) {
        try {
            this.id = json.getString("id");
            this.name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter de l'identifiant du mode de vibration.
     * @return Identifiant du mode de vibration
     */
    public String getId() {
        return id;
    }

    /**
     * Getter du nom du mode de vibration.
     * @return Nom du mode de vibration
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom du mode de vibration.
     * @param name Nom du mode de vibration
     */
    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    /**
     * Renvoie le mode de vibration par défaut (N/A).
     * @return Mode de vibration par défaut
     */
    public static VibrationMode getDefaultVibrationMode() {
        return new VibrationMode("N/A","N");
    }

    /**
     * Convertit le mode de vibration en un objet JSON.
     * @return Objet JSON
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Crée un objet VibrationMode à partir d'un objet JSON.
     * @param json Objet JSON
     * @return Objet VibrationMode
     */
    public static VibrationMode fromJson(JSONObject json) {
        return new VibrationMode(json);
    }

    /**
     * Enregistre le mode de vibration dans les préférences partagées.
     * @param context Contexte de l'application
     */
    public void saveVibrationMode(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("vibration_modes", Context.MODE_PRIVATE);

        JSONObject json = this.toJson();

        sharedPreferences.edit().putString(this.getId(), json.toString()).apply();
    }

    /**
     * Récupère les modes de vibrations enregistrés dans les préférences partagées.
     * @param context Contexte de l'application
     * @return Liste de modes de vibrations enregistrés
     */
    public static List<VibrationMode> getSavedVibrationModes(Context context) {
        List<VibrationMode> vibrationModes = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("vibration_modes", Context.MODE_PRIVATE);

        Set<String> keys = sharedPreferences.getAll().keySet();

        for (String key : keys) {
            String jsonString = sharedPreferences.getString(key, null);
            if (jsonString != null) {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    VibrationMode vibrationMode = VibrationMode.fromJson(json);
                    vibrationModes.add(vibrationMode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return vibrationModes;
    }

    /**
     * Récupère le mode de vibration enregistré correspondant à l'id donné en paramètres
     * @param context Contexte de l'application
     * @param id l'identifiant du mode de vibration que l'on souhaite récupérer
     * @return Mode de vibration d'identifiant id
     */
    public static VibrationMode getSavedVibrationModeFromId(Context context, String id) {
        List<VibrationMode> vibrationModes = getSavedVibrationModes(context);
        Map<String, VibrationMode> vibrationModeMap = new HashMap<>();

        for (VibrationMode vibrationMode : vibrationModes) {
            vibrationModeMap.put(vibrationMode.getId(), vibrationMode);
        }

        return vibrationModeMap.get(id);
    }

}
