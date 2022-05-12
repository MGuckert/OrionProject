package com.example.tactigant20.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.tactigant20.R;
public class InfoActivity extends AppCompatActivity {

    private static final String TAG_INFO = "DebugInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_INFO,"Appel de onCreate dans InfoActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }
}