package com.example.tactigant20.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Scanner;

/**
 * Classe contenant les méthodes permettant de sauvegarder et de charger les modes de vibrations de chaque application.
 * Cet objet permet de coordonner les notifications reçues avec un mode de vibration
 *
 * @author Lucas S.
 * @since 1.0
 */
public class VibrationsTool {

    private static final String TAG_VT = "debug_vibration_tool";

    private final WeakReference<Context> mContext;

    /**
     * Constructeur unique du <i>VibrationTool</i>
     *
     * @param mContext le contexte dans lequel le <i>VibrationTool</i> est instancié
     */
    public VibrationsTool(Context mContext) {
        this.mContext = new WeakReference<>(mContext); // En pratique toujours MainActivity
    }

    /**
     * Méthode qui permet de sauvegarder le mode de vibration configuré pour une application donnée dans un fichier de données au format JSON.
     * Si le nom de l'application est déjà présent dans le fichier, alors son mode de vibration est mis à jour.
     *
     * @param packageName   le nom du package de l'application pour laquelle on souhaite sauvegarder le mode de vibration
     * @param vibrationMode le mode de vibration à sauvegarder pour l'application
     * @throws JSONException si une erreur se produit lors de la manipulation du fichier au format JSON
     */
    public void saveAppVibrationModeId(String packageName, String vibrationMode) throws JSONException {
        File file = new File(mContext.get().getFilesDir(), "vibration_modes_data.json");

        try {
            if (file.createNewFile()) {
                Log.d(TAG_VT, "Fichier créé");
            } else {
                Log.w(TAG_VT, "Le fichier existe déjà");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Lecture du fichier et "transformation" en objet JSON
        JSONObject root = new JSONObject();
        try {
            Scanner sc = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (sc.hasNextLine()) {
                builder.append(sc.nextLine());
            }
            String fileContents = builder.toString();
            if (!fileContents.isEmpty()) {
                root = new JSONObject(fileContents);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        // Ajoute ou met à jour le mode de vibration pour le packageName donné
        root.put(packageName, vibrationMode);

        //Ecriture de l'objet JSON mis à jour dans le fichier
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(root.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de charger le fichier vibration_modes_data.json dans un objet JSONObject
     *
     * @param context le contexte de l'application
     * @return le JSONObject contenant les données du fichier vibration_modes_data.json
     */
    public JSONObject loadAppsVibrationModesId(Context context) {
        File file = new File(context.getFilesDir(), "vibration_modes_data.json");

        try {
            if (file.createNewFile()) {
                Log.d(TAG_VT, "Fichier créé");
            } else {
                Log.w(TAG_VT, "Le fichier existe déjà");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject root = new JSONObject();
        try {
            Scanner sc = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (sc.hasNextLine()) {
                builder.append(sc.nextLine());
            }
            root = new JSONObject(builder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    /**
     * Méthode utilisant <i>loadVibrationModes()</i> et renvoyant le mode de vibration de l'application ayant pour package <i>packageName</i>
     *
     * @param packageName le nom du package de l'application
     * @param context     le contexte de l'application
     * @return le JSONObject contenant les données du fichier vibration_modes_data.json
     */
    public String loadAppVibrationModeId(String packageName, Context context) {
        JSONObject root = loadAppsVibrationModesId(context);
        return root.optString(packageName, "N");
    }
}