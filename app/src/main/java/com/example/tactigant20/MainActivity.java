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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tactigant20.databinding.ActivityMainBinding;
import com.example.tactigant20.ui.home.HomeFragment;
import com.example.tactigant20.ui.notifications.AppInfo;
import com.example.tactigant20.ui.notifications.NotificationsFragment;
import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsMain;
import com.example.tactigant20.ui.vibrations.VibrationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    NotificationsFragment notificationsFragment;
    MenuItem prevMenuItem;

    ViewPager2 myViewPager2;
    Adapter myAdapter;

    private static final String TAG_MAIN = "DebugMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        Log.d(TAG_MAIN, "Appel de onCreate dans MainActivity");
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        com.example.tactigant20.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Création de la toolbar
        Toolbar topAppBar=findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Création du système de swipe
        myViewPager2 = findViewById(R.id.vpPager);
        myAdapter = new Adapter(getSupportFragmentManager(), getLifecycle());
        myAdapter.addFragment(new VibrationsFragment());
        myAdapter.addFragment(new HomeFragment());
        myAdapter.addFragment(new NotificationsFragment());

        // Création de la barre de navigation du bas
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnItemSelectedListener(navListener);

        myViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // Cette fonction permet à la BottomNavigationView et au ViewPager2 de considérer l'un et l'autre
            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNav.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page",   ""+position);
                bottomNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNav.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(myAdapter);
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

    // Ceci permet au "swiping" de savoir comment alterner entre les fragments
    public static class Adapter extends FragmentStateAdapter {

        private final ArrayList<Fragment> fragmentList = new ArrayList<>();

        public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG_MAIN, "Appel de createFragment dans Adapter");
            return fragmentList.get(position);
        }

        public void addFragment(Fragment fragment) {
            Log.d(TAG_MAIN, "Appel de addFragment dans Adapter");
            fragmentList.add(fragment);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG_MAIN, "Appel de getItemcount dans Adapter");
            return fragmentList.size();
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

    /*
    public static void cancelNotification(Context ctx, int notifyId) {
        // Permet de supprimer la notif. Mettre ctx=this et notifyId=100
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

     */

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

    private void writeInFile(String s, int mode) {
        // On vient éditer le fichier "vibration_modes_data"
        //Si le mode est Context.MODE_PRIVATE : si le fichier existe, il est remplacé, sinon un nouveau fichier est créé.
        //Si le mode est Context.MODE_APPEND : si le fichier existe alors les données sont ajoutées à la fin du fichier.
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("vibration_modes_data.txt", mode);
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

    private void saveVibrationMode(String packageName, String vibrationMode) {
        //On sauvegarde le mode de vibration "vibrationMode" pour l'application "packageName"
        //On lit d'abord le fichier en ajoutant chaque ligne dans une String "fileData" tant que la ligne correspond à l'app n'a pas été trouvée
        //Si on atteint la fin du fichier, alors on ajoute la ligne adaptée à la fin
        //Sinon, on remplace la ligne correspondante, puis on rajoute toutes les lignes d'après à fileData; enfin, on utilise writeInFile avec MODE_PRIVATE pour remplacer
        //le contenu du fichier (seule la ligne correspondante à "packageName" a changé)
        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput("vibration_modes_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder fileData = new StringBuilder(); //Chaîne de caractères dans laquelle on stocke les lignes du fichier qu'on ne modifie pas
        if (inputStream != null) {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(inputReader);

            String line = null;
            do {
                try {
                    line = buffReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileData.append(line).append("\n");
            } while (line != null && (line.length() <= packageName.length() || !line.startsWith(packageName)));
            if (line != null) { //Si line n'est pas nulle, c'est que line contient la ligne qui nous intéresse
                fileData = new StringBuilder(fileData.substring(0, fileData.length() - line.length() - 1)); //On la retire de fileData, et on la remplace par celle avec le bon mode de vibration
                fileData.append(packageName).append(" : ").append(vibrationMode).append("\n");
                do { //On récupère alors les autres lignes
                    try {
                        line = buffReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line != null)
                        fileData.append(line).append("\n");
                } while (line != null);
                System.err.println("FileData: \n" + fileData);
                writeInFile(fileData.toString(),MODE_PRIVATE); // On réécrit le fichier en ayant changé la bonne ligne
            }
            else
                writeInFile(packageName + " : " + vibrationMode + "\n", MODE_APPEND); //Sinon, on ajoute simplement la ligne à la fin du fichier
        }
        try {
            assert inputStream != null;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        AppInfo currentItem = this.notificationsFragment.getCurrentItem();
        Dialog dialog = this.notificationsFragment.getDialog();
        int boutonRadio = view.getId();
        if (boutonRadio == R.id.radioButtonNA) {
            if (checked) {
                currentItem.vibrationMode = "N";
            }
        }
        if (boutonRadio == R.id.radioButtonMode1) {
            if (checked) {
                currentItem.vibrationMode = "1";
            }
        }
        if (boutonRadio == R.id.radioButtonMode2) {
            if (checked) {
                currentItem.vibrationMode = "2";
            }
        }
        if (boutonRadio == R.id.radioButtonMode3) {
            if (checked) {
                currentItem.vibrationMode = "3";
            }
        }

        this.notificationsFragment.setFromIndex(this.notificationsFragment.getCurrentItemPosition(), currentItem);
        this.notificationsFragment.getAdapter().notifyDataSetChanged();
        this.saveVibrationMode(currentItem.info.packageName,currentItem.vibrationMode);
        dialog.dismiss();
    }
}
