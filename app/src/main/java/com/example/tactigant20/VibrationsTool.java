package com.example.tactigant20;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class VibrationsTool {

    private WeakReference<Context> mContext;

    public VibrationsTool(Context mContext) {
        this.mContext = new WeakReference<>(mContext); // En pratique toujours MainActivity
    }

    public String loadVibrationMode(String notifName, Context context) {
        //Fonction renvoyant le mode de vibration de l'application qui a pour package "notifName" sauvegardé dans le fichier
        // "vibration_modes_data.txt", et "UNKNOWN" si aucune donnée pour cette application n'a été sauvegardée.
        FileInputStream inputStream = null;
        try {
            inputStream = context.openFileInput("vibration_modes_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(inputReader);

            String line = null;
            do { //On lit le fichier ligne par ligne, en comparant le début de chaque ligne avec "notifName" :
                // s'il est identique, on est sur la bonne ligne, et on peut renvoyer le mode de vibration écrit !
                try {
                    line = buffReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.err.println(line);
                int n = notifName.length();
                if ( line != null && n < line.length()) {
                    if (line.substring(0, n).equals(notifName)) {
                        return line.substring(line.length()-1);
                    }
                }
            } while (line != null);
        }
        return "UNKNOWN";
    }

    public void saveVibrationMode(String packageName, String vibrationMode) {
        //On sauvegarde le mode de vibration "vibrationMode" pour l'application "packageName"
        //On lit d'abord le fichier en ajoutant chaque ligne dans une String "fileData" tant que la ligne correspond à l'app n'a pas été trouvée
        //Si on atteint la fin du fichier, alors on ajoute la ligne adaptée à la fin
        //Sinon, on remplace la ligne correspondante, puis on rajoute toutes les lignes d'après à fileData; enfin, on utilise writeInFile avec MODE_PRIVATE pour remplacer
        //le contenu du fichier (seule la ligne correspondante à "packageName" a changé)
        FileInputStream inputStream = null;
        try {
            inputStream = mContext.get().openFileInput("vibration_modes_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder fileData = new StringBuilder(); //Chaîne de caractères dans laquelle on stocke les lignes du fichier qu'on ne modifie pas
        if (inputStream != null) {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(inputReader);

            String line = null;
            do {
                try {
                    line = buffReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileData.append(line).append("\n");
            } while (line != null && (line.length() <= packageName.length() || !line.startsWith(packageName)));
            if (line != null) { //Si line n'est pas nulle, c'est que line contient la ligne qui nous intéresse
                fileData = new StringBuilder(fileData.substring(0, fileData.length() - line.length() - 1)); //On la retire de fileData, et on la remplace par celle avec le bon mode de vibration
                fileData.append(packageName).append(" : ").append(vibrationMode).append("\n");
                do { //On récupère alors les autres lignes
                    try {
                        line = buffReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line != null)
                        fileData.append(line).append("\n");
                } while (line != null);
                System.err.println("FileData: \n" + fileData);
                writeInFile(fileData.toString(),MODE_PRIVATE); // On réécrit le fichier en ayant changé la bonne ligne
            }
            else
                writeInFile(packageName + " : " + vibrationMode + "\n", MODE_APPEND); //Sinon, on ajoute simplement la ligne à la fin du fichier
        }
        try {
            assert inputStream != null;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeInFile(String s, int mode) {
        // On vient éditer le fichier "vibration_modes_data"
        //Si le mode est Context.MODE_PRIVATE : si le fichier existe, il est remplacé, sinon un nouveau fichier est créé.
        //Si le mode est Context.MODE_APPEND : si le fichier existe alors les données sont ajoutées à la fin du fichier.
        FileOutputStream fos = null;
        try {
            fos = mContext.get().openFileOutput("vibration_modes_data.txt", mode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            assert fos != null;
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
