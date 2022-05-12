package com.example.tactigant20.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.tactigant20.R;

public class SettingsMain extends AppCompatActivity {

    private static final String TAG_SETTINGS = "DebugSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_SETTINGS,"Appel de onCreate dans SettingsMain");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
    }
}