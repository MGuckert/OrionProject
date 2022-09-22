package com.example.tactigant20.ui.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tactigant20.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG_SETTINGS = "debug_settings_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}