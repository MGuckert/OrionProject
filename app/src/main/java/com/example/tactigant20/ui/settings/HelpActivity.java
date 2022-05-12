package com.example.tactigant20.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.tactigant20.R;
public class HelpActivity extends AppCompatActivity {

    private static final String TAG_HELP = "DebugHelpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_HELP,"Appel de onCreate dans HelpActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
}