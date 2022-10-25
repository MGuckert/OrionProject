package com.example.tactigant20;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tactigant20.databinding.ActivityMainBinding;
import com.example.tactigant20.model.BluetoothLowEnergyTool;
import com.example.tactigant20.model.SwipeAdapter;
import com.example.tactigant20.model.VibrationsTool;
import com.example.tactigant20.ui.NotificationTool;
import com.example.tactigant20.ui.fragments.HomeFragment;
import com.example.tactigant20.ui.fragments.NotificationsFragment;
import com.example.tactigant20.ui.fragments.VibrationsFragment;
import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "debug_main_activity";

    private static final String ADRESSE = "94:3C:C6:06:CC:1E";
    private static VibrationsTool myVibrationsTool;
    private static BluetoothLowEnergyTool myBLET;
    private static NotificationTool myNotificationTool;
    private final VibrationsFragment vibrationsFragment = new VibrationsFragment();
    private final HomeFragment homeFragment = new HomeFragment();
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private ViewPager2 myViewPager2;
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {

        int itemId = item.getItemId();
        if (itemId == R.id.navigation_vibrations) {
            myViewPager2.setCurrentItem(0);
        } else if (itemId == R.id.navigation_home) {
            myViewPager2.setCurrentItem(1);
        } else if (itemId == R.id.navigation_notifications) {
            myViewPager2.setCurrentItem(2);
        }

        return true;
    };
    private MenuItem prevMenuItem;

    public static VibrationsTool getMyVibrationsTool() {
        return myVibrationsTool;
    }

    public static BluetoothLowEnergyTool getMyBLET() {
        return myBLET;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.tactigant20.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myVibrationsTool = new VibrationsTool(this);
        myBLET = new BluetoothLowEnergyTool(ADRESSE, this);

        myNotificationTool = new NotificationTool(this,
                "ID_TACTIGANT",
                100,
                "Chaîne de notification Orion");

        myNotificationTool.createNotificationChannel("Batterie : ?%");

        // On demande à l'utilisateur d'activer le Bluetooth si nécessaire
        if (myBLET.getAdapter() == null || !myBLET.getAdapter().isEnabled()) {
            ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                    });
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult.launch(enableBtIntent);
        }

        // Création de la toolbar
        Toolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Création du système de swipe
        myViewPager2 = findViewById(R.id.vpPager);
        SwipeAdapter mySwipeAdapter = new SwipeAdapter(getSupportFragmentManager(), getLifecycle());
        mySwipeAdapter.addFragment(vibrationsFragment);
        mySwipeAdapter.addFragment(homeFragment);
        mySwipeAdapter.addFragment(notificationsFragment);

        // Création de la barre de navigation du bas
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnItemSelectedListener(navListener);
        myViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            // Cette fonction permet à la BottomNavigationView et au ViewPager2 de considérer l'un et l'autre
            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNav.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "" + position);
                bottomNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNav.getMenu().getItem(position);

            }

        });
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(mySwipeAdapter);
        myViewPager2.setCurrentItem(1, false); // On commence sur HomeFragment

    }

    // Création du menu de la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // Gestion des boutons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) { // "switch" au cas où on en rajoute
            case (R.id.action_help):
                Log.d(TAG_MAIN, "Action : action_help");
                openHelp();
                return true;
            case (R.id.action_info):
                Log.d(TAG_MAIN, "Action : action_info");
                openInfo();
                return true;
            case (R.id.action_settings):
                Log.d(TAG_MAIN, "Action : action_settings");
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openHelp() {
        Log.d(TAG_MAIN, "appel de openHelp dans MainActivity");
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void openInfo() {
        Log.d(TAG_MAIN, "appel de openInfo dans MainActivity");
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Log.d(TAG_MAIN, "appel de openSettings dans MainActivity");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        myVibrationsTool = null;
        myBLET = null;
        HomeFragment.getMtHFCustomUIThread().setRunning(false);
        NotificationTool.getCustomUIThread().setRunning(false);
        myNotificationTool.clearNotification();
        super.onDestroy();
    }
}
