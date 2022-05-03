package com.example.tactigant20;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsMain;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tactigant20.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageView settingsIcon;
    private ImageView helpIcon;
    private ImageView infoIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        //        .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Settings button on toolbar
        settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(view -> openSettings());
        //Helps button on toolbar
        helpIcon = findViewById(R.id.helpicon);
        helpIcon.setOnClickListener(view -> openhelp());
        //Information button on toolbar
        infoIcon = findViewById(R.id.infoicon);
        infoIcon.setOnClickListener(view -> openinfo());
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsMain.class);
        startActivity(intent);
    }
    private void openhelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    private void openinfo(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}