package com.example.tactigant20.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;

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
}