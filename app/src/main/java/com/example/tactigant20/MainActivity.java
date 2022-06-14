package com.example.tactigant20;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tactigant20.databinding.ActivityMainBinding;
import com.example.tactigant20.ui.home.HomeFragment;
import com.example.tactigant20.ui.notifications.NotificationsFragment;
import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsMain;
import com.example.tactigant20.ui.vibrations.VibrationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    BottomNavigationView bottomNavigationView;
    //viewPager
    private ViewPager viewPager;

    //Fragments
    VibrationsFragment vibrationsFragment;
    HomeFragment menuFragment;
    NotificationsFragment notificationsFragment;
    MenuItem prevMenuItem;

    private static final String TAG_MAIN = "DebugMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_MAIN, "Appel de onCreate dans MainActivity");
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Création de la toolbar
        Toolbar topAppBar=findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.vpPager);

        // Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_vibrations:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_home:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.navigation_notifications:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page",""+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
        // Paramètres de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Status du bracelet : CONNECTED/DISCONNECTED")
                .setContentText("Batterie: 50 %")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Fais apparaitre la notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        vibrationsFragment=new VibrationsFragment();
        menuFragment=new HomeFragment();
        notificationsFragment=new NotificationsFragment();

        adapter.addFragment(vibrationsFragment);
        adapter.addFragment(menuFragment);
        adapter.addFragment(notificationsFragment);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    // Création du menu de la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG_MAIN,"appel de onCreateOptionsMenu dans MainActivity");
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // Gestion des boutons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG_MAIN,"appel de onOptionsItemSelected dans MainActivity");
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
        Intent intent = new Intent(this, SettingsMain.class);
        startActivity(intent);
    }
    public static void cancelNotification(Context ctx, int notifyId) {
        // Permet de supprimer la notif. Mettre ctx=this et notifyId=100
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
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
    private String queltype(String NomNotif) {
        // Retourne le type de vibration associé à la notification "NomNotif" dans le fichier "enregistrement"
        String filePath = "enregistrement.txt"; // Probablement à changer pour mettre quelque chose de plus précis (si ça ne marche pas comme ça)
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String notif = "";
        String type = "";
        int i = 0;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            // On extrait l'information concernant la notifs
            while (line.charAt(i) != ' ') {
                notif += line.charAt(i);
                i++;
            }

            // On inverse la chaine "notif" parce qu'elle a été construite dans le mauvais sens
            StringBuilder strb = new StringBuilder(notif);
            notif = strb.reverse().toString();
            // On verifie si elle correspond à "NomNotifs"
            if (notif.equals(NomNotif)) {
                // On extrait l'information concernant le type
                i = i + 3;
                while (line.charAt(i) != '\n') {
                    type += line.charAt(i);
                    i++;
                }
                // On inverse la chaine "type" parce qu'elle a été construite dans le mauvais sens
                StringBuilder strb2 = new StringBuilder(type);
                type = strb2.reverse().toString();
                // On renvoie le type associé à la notification "NomNotif"
                return type;
            }
            i = 0;
        }
        return "PAS TROUVE";
    }


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
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
