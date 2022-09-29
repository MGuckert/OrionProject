package com.example.tactigant20;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tactigant20.databinding.ActivityMainBinding;
import com.example.tactigant20.model.AppInfo;
import com.example.tactigant20.model.BluetoothLowEnergyTool;
import com.example.tactigant20.model.SwipeAdapter;
import com.example.tactigant20.model.VibrationsTool;
import com.example.tactigant20.ui.home.HomeFragment;
import com.example.tactigant20.ui.notifications.NotificationsFragment;
import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsActivity;
import com.example.tactigant20.ui.vibrations.VibrationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "debug_main_activity";

    private static final String ADRESSE = "94:3C:C6:06:CC:1E";

    private ViewPager2 myViewPager2;
    private final VibrationsFragment vibrationsFragment = new VibrationsFragment();
    private final HomeFragment homeFragment = new HomeFragment();
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private MenuItem prevMenuItem;

    private static VibrationsTool myVibrationsTool;
    private static BluetoothLowEnergyTool myBLET;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        com.example.tactigant20.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myVibrationsTool = new VibrationsTool(this);
        myBLET = new BluetoothLowEnergyTool(ADRESSE, this);

        // Création de la toolbar
        Toolbar topAppBar=findViewById(R.id.topAppBar);
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
                Log.d("page",   ""+position);
                bottomNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNav.getMenu().getItem(position);

            }

        });
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(mySwipeAdapter);
        myViewPager2.setCurrentItem(1,false); // On commence sur HomeFragment

        // Paramètres de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Status du bracelet : CONNECTED/DISCONNECTED")
                .setContentText("Batterie: 50 %")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Fait apparaitre la notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());

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

        switch(id) { // "switch" au cas où on en rajoute
            case (R.id.action_help):
                Log.d(TAG_MAIN,"Action : action_help");
                openHelp();
                return true;
            case (R.id.action_info):
                Log.d(TAG_MAIN,"Action : action_info");
                openInfo();
                return true;
            case (R.id.action_settings):
                Log.d(TAG_MAIN,"Action : action_settings");
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

    /*
    public static void cancelNotification(Context ctx, int notifyId) {
        // Permet de supprimer la notif. Mettre ctx=this et notifyId=100
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

     */

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tactigant channel";
            String description = "Tactigant Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }





/*
    private void stockage(String s, int mode) {
        // On vient éditer le fichier "enregistrement"
        //Si le mode est Context.MODE_PRIVATE : si le fichier existe, il est remplacé, sinon un nouveau fichier est créé.
        //Si le mode est Context.MODE_APPEND : si le fichier existe alors les données sont ajoutées à la fin du fichier.
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("enregistrement.txt", mode);
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

 */

    //Fonction qui gère le choix d'un mode de vibration dans la fenêtre pop-up du fragment notifications
    public void onRadioButtonClicked(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();
        // Check which radio button was clicked
        AppInfo currentItem = this.notificationsFragment.getCurrentItem();
        Dialog dialog = this.notificationsFragment.getDialog();
        int boutonRadio = v.getId();
        if (boutonRadio == R.id.radioButtonNA) {
            if (checked) {
                currentItem.setVibrationMode("N");
            }
        }
        if (boutonRadio == R.id.radioButtonMode1) {
            if (checked) {
                currentItem.setVibrationMode("1");
            }
        }
        if (boutonRadio == R.id.radioButtonMode2) {
            if (checked) {
                currentItem.setVibrationMode("2");
            }
        }
        if (boutonRadio == R.id.radioButtonMode3) {
            if (checked) {
                currentItem.setVibrationMode("3");
            }
        }

        this.notificationsFragment.setFromIndex(this.notificationsFragment.getCurrentItemPosition(), currentItem);
        this.notificationsFragment.getAdapter().notifyDataSetChanged();
        myVibrationsTool.saveVibrationMode(currentItem.getInfo().packageName,currentItem.getVibrationMode());
        dialog.dismiss();
    }

    public static VibrationsTool getMyVibrationsTool() {
        return myVibrationsTool;
    }

    public static BluetoothLowEnergyTool getMyBLET() {
        return myBLET;
    }
}
