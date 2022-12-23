package com.example.tactigant20.ui.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.ui.fragments.NotificationsFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Classe de l'activité de paramètres de l'application. Cette activité permet de changer l'adresse MAC
 * de la montre connectée à l'application et de réinitialiser les modes de vibration des notifications.
 * Elle permet également d'activer ou désactiver le mode sombre de l'application.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG_SETTINGS = "debug_settings_activity";

    private EditText settingsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsEditText = findViewById(R.id.settings_editText);
        settingsEditText.setText(getSharedPreferences("PREFS", MODE_PRIVATE).getString("PREFS_MAC", getString(R.string.MAC_default)));

        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this::cSettingsButton);

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(this::cResetButton);

        //Switch mode sombre
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch darkModeSwitch = findViewById(R.id.dark_mode_switch);

        darkModeSwitch.setChecked(isDarkModeEnabled());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableDarkMode();
            } else {
                disableDarkMode();
            }
        });
    }

    /**
     * Cette méthode permet de réinitialiser les modes de vibrations de toutes les applications enregistrées dans l'application.
     * Elle affiche une fenêtre de confirmation avant de réinitialiser les données. Si l'utilisateur confirme, elle vide de son contenu le fichier "vibration_modes_data.json"
     * et met à jour le flag de réinitialisation des données dans le fragment "NotificationsFragment".
     *
     * @param v la vue actuelle (un bouton)
     */
    private void cResetButton(View v) {
        Dialog confirmDialog = new Dialog(this);
        // Set the title and content of the Dialog
        confirmDialog.setTitle("Confirm Action");
        confirmDialog.setContentView(R.layout.confirm_reset_dialog);
        // Show the Dialog
        confirmDialog.show();
        Button yesButton = confirmDialog.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(view -> {
            File file = new File(v.getContext().getFilesDir(), "vibration_modes_data.json");
            if (file.exists() && !file.isDirectory()) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(new JSONObject().toString());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            NotificationsFragment.setDataReinitialised(true);
            confirmDialog.dismiss();
            Toast.makeText(v.getContext(), "Modes de vibration réinitialisés", Toast.LENGTH_SHORT).show();
        });

        Button noButton = confirmDialog.findViewById(R.id.no_button);
        noButton.setOnClickListener(view -> confirmDialog.dismiss());
    }

    /**
     * Méthode qui gère le clic sur le bouton de modification de l'adresse MAC.
     * Elle vérifie la validité de l'adresse MAC saisie, la sauvegarde et affiche un message de confirmation ou d'erreur.
     *
     * @param v la vue associée au bouton
     */
    private void cSettingsButton(View v) {
        String NewMAC = settingsEditText.getText().toString().toUpperCase();
        if (Pattern.matches("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$", NewMAC)) {
            settingsEditText.setText(NewMAC);
            MainActivity.getMyBLET().setAdresseMAC(NewMAC);
            getSharedPreferences("PREFS", MODE_PRIVATE).edit().putString("PREFS_MAC", NewMAC).apply();
            Log.d(TAG_SETTINGS, "Sauvegarde de la MAC " + NewMAC);
            Toast.makeText(this, "Nouvelle MAC : " + NewMAC, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.MAC_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode qui vérifie si le mode sombre est activé.
     *
     * @return true si le mode sombre est activé, false sinon
     */
    private boolean isDarkModeEnabled() {
        return (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    }

    /**
     * Méthode qui active le mode sombre.
     */
    private void enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getDelegate().applyDayNight();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("night_mode", AppCompatDelegate.MODE_NIGHT_YES).apply();
    }

    /**
     * Méthode qui désactive le mode sombre.
     */
    private void disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO).apply();
    }
}