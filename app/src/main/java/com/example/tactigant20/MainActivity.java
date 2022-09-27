package com.example.tactigant20;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.example.tactigant20.databinding.ActivityMainBinding;
import com.example.tactigant20.ui.home.HomeFragment;
import com.example.tactigant20.ui.notifications.AppInfo;
import com.example.tactigant20.ui.notifications.NotificationsFragment;
import com.example.tactigant20.ui.settings.HelpActivity;
import com.example.tactigant20.ui.settings.InfoActivity;
import com.example.tactigant20.ui.settings.SettingsMain;
import com.example.tactigant20.ui.vibrations.VibrationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
        viewPager = findViewById(R.id.vpPager);

        // Initializing the bottomNavigationView
        bottomNavigationView = findViewById(R.id.nav_view);

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
        String fileData = ""; //Chaîne de caractères dans laquelle on stocke les lignes du fichier qu'on ne modifie pas
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
                fileData += line + "\n";
            } while (line != null && (line.length() <= packageName.length() || !line.substring(0,packageName.length()).equals(packageName)));
            if (line != null) { //Si line n'est pas nulle, c'est que line contient la ligne qui nous intéresse
                fileData = fileData.substring(0,fileData.length()-line.length()-1); //On la retire de fileData, et on la remplace par celle avec le bon mode de vibration
                fileData += packageName + " : " + vibrationMode + "\n";
                do { //On récupère alors les autres lignes
                    try {
                        line = buffReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line != null)
                        fileData += line + "\n";
                } while (line != null);
                System.err.println("FileData: \n" + fileData);
                writeInFile(fileData,MODE_PRIVATE); // On réécrit le fichier en ayant changé la bonne ligne
            }
            else
                writeInFile(packageName + " : " + vibrationMode + "\n", MODE_APPEND); //Sinon, on ajoute simplement la ligne à la fin du fichier
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            assert fos != null;
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Fonction qui gère le choix d'un mode de vibration dans la fenêtre pop-up du fragment notifications
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        AppInfo currentItem = this.notificationsFragment.getCurrentItem();
        Dialog dialog = this.notificationsFragment.getDialog();
        switch (view.getId()) {
            case R.id.radioButtonNA:
                if (checked)
                    currentItem.vibrationMode = "N";
                break;
            case R.id.radioButtonMode1:
                if (checked)
                    currentItem.vibrationMode = "1";
                break;
            case R.id.radioButtonMode2:
                if (checked)
                    currentItem.vibrationMode = "2";
                break;
            case R.id.radioButtonMode3:
                if (checked)
                    currentItem.vibrationMode = "3";
                break;
        }
        this.notificationsFragment.setFromIndex(this.notificationsFragment.getCurrentItemPosition(), currentItem);
        this.notificationsFragment.getAdapter().notifyDataSetChanged();
        this.saveVibrationMode(currentItem.info.packageName,currentItem.vibrationMode);
        dialog.dismiss();
    }
}
