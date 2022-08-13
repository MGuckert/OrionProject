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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        com.example.tactigant20.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Création de la toolbar
        Toolbar topAppBar=findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        //Initializing viewPager
        viewPager = findViewById(R.id.vpPager);

        // Initializing the bottomNavigationView
        bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
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

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @NonNull
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

    /*
    public static void cancelNotification(Context ctx, int notifyId) {
        // Permet de supprimer la notif. Mettre ctx=this et notifyId=100
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
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
    private String quelMode(String NomNotif) {
        // Retourne le mode de vibration associé à la notification "NomNotif" dans le fichier "enregistrement"
        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput("enregistrement.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int content = 0;
        StringBuilder notif= new StringBuilder();
        String mode="";
        Log.d("Storage","Initialisation....");
        while (true){
            try {
                assert inputStream != null;
                if ((content = inputStream.read()) == -1) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //On extrait l'information concernant la notifs
            if((char)content !=' ') {
                notif.append((char) content);
            }
            else{
                Log.d("Storage","Notifs : "+notif);
                // On verifie si elle correspond à "NomNotifs"
                if (notif.toString().equals(NomNotif)) {
                    Log.d("Storage","Correspond !");
                    // On avance de 2 caractéres (: ) content
                    try {
                        content=inputStream.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        content=inputStream.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // On extrait l'information concernant le mode
                    while (true) {
                        try {
                            if ((content = inputStream.read()) == -1) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //On extrait l'information concernant la notifs
                        if ((char) content != '\n') {
                            mode = mode + (char) content;
                        }
                        else {
                            Log.d("Storage",mode);
                            return mode;
                        }
                    }
                }
                else {
                    Log.d("Storage","Ne correspond pas !");
                    try {
                        content = inputStream.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //On finit d'extraire la fin de la ligne qui ne sert plus à rien
                    while ((char) content != '\n') {
                        try {
                            content = inputStream.read();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    notif = new StringBuilder();
                }
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "UNKNOWN";

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

 */

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
                    currentItem.vibrationMode = "NA";
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
        dialog.dismiss();
    }
}
