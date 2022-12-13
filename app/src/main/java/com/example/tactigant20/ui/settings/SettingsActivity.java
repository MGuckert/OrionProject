package com.example.tactigant20.ui.settings;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.ui.fragments.HomeFragment;

import java.util.regex.Pattern;

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


        //Switch mode sombre
        Switch darkModeSwitch = findViewById(R.id.dark_mode_switch);

        darkModeSwitch.setChecked(isDarkModeEnabled());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableDarkMode();
            } else {
                disableDarkMode();
            }
        });
    }

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

    // Méthode pour vérifier si le mode sombre est activé
    private boolean isDarkModeEnabled() {
        return (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    }

    // Méthode pour activer le mode sombre
    private void enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getDelegate().applyDayNight();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("night_mode", AppCompatDelegate.MODE_NIGHT_YES).apply();
    }

    // Méthode pour désactiver le mode sombre
    private void disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO).apply();
    }
}