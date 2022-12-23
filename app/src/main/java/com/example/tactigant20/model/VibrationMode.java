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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public static VibrationMode getDefaultVibrationMode() {
        return new VibrationMode("N/A","N");
    }

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

    public static VibrationMode fromJson(JSONObject json) {
        return new VibrationMode(json);
    }

    public void saveVibrationMode(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("vibration_modes", Context.MODE_PRIVATE);

        JSONObject json = this.toJson();

        sharedPreferences.edit().putString(this.getId(), json.toString()).apply();
    }

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

    public static VibrationMode getSavedVibrationModeFromId(Context context, String id) {
        List<VibrationMode> vibrationModes = getSavedVibrationModes(context);
        Map<String, VibrationMode> vibrationModeMap = new HashMap<>();

        for (VibrationMode vibrationMode : vibrationModes) {
            vibrationModeMap.put(vibrationMode.getId(), vibrationMode);
        }

        return vibrationModeMap.get(id);
    }

}
