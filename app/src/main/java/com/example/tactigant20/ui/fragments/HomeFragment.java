package com.example.tactigant20.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private static final String TAG_HOME = "debug_home_fragment";

    private ImageView imageConfirmationConnexion;
    private ImageView imageConfirmationDeconnexion;
    private TextView texteDeChargement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        com.example.tactigant20.databinding.FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Boutons pour le Bluetooth
        // Lance le scan et se connecte à la carte si possible
        Button scanButton = root.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this::cScanButton);
        // Ouvre les paramètres Bluetooth
        Button bluetoothSettingsButton = root.findViewById(R.id.bluetoothSettingsButton);
        bluetoothSettingsButton.setOnClickListener(this::cBluetoothSettingsButton);
        // Se déconnecte de la carte
        Button deconnectionButton = root.findViewById(R.id.deconnexionButton);
        deconnectionButton.setOnClickListener(this::cDeconnectionButton);

        // Texte de chargement
        texteDeChargement = root.findViewById(R.id.texteDeChargement);

        // Image de confirmation de connexion
        imageConfirmationConnexion = root.findViewById(R.id.connexionValide);

        // Image d'erreur/absence de connexion
        imageConfirmationDeconnexion = root.findViewById(R.id.connexionInvalide);


        CustomUIThread myCustomUIThread = new CustomUIThread();
        myCustomUIThread.start();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cScanButton(View v) {
        Log.d(TAG_HOME, "Bouton Scan pressé");
        MainActivity.getMyBLET().scan();
    }

    private void cBluetoothSettingsButton(View v) {
        Log.d(TAG_HOME, "Bouton paramètres Bluetooth pressé");
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    private void cDeconnectionButton(View v) {
        Log.d(TAG_HOME, "Bouton déconnexion pressé");
        MainActivity.getMyBLET().disconnect();
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public class CustomUIThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG_HOME, "Lancement du thread");

            while(true) {
                //Log.d(TAG_HOME, "\nmValeurDeChargement : " + myBLET.getValeurDeChargement() +"\nmValeurDeConnection : " + myBLET.getValeurDeConnexion());
            try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            switch (MainActivity.getMyBLET().getValeurDeConnexion()) {
                case DECONNECTE:
                    new Handler(Looper.getMainLooper()).post(() -> {
                        imageConfirmationConnexion.setVisibility(View.INVISIBLE);
                        imageConfirmationDeconnexion.setVisibility(View.VISIBLE);
                        texteDeChargement.setVisibility(View.INVISIBLE);
                    });
                    break;
                case CHARGEMENT:
                    new Handler(Looper.getMainLooper()).post(() -> {
                        imageConfirmationConnexion.setVisibility(View.INVISIBLE);
                        imageConfirmationDeconnexion.setVisibility(View.INVISIBLE);
                        texteDeChargement.setVisibility(View.VISIBLE);
                    });
                    break;
                case CONNECTE:
                    new Handler(Looper.getMainLooper()).post(() -> {
                        imageConfirmationConnexion.setVisibility(View.VISIBLE);
                        imageConfirmationDeconnexion.setVisibility(View.INVISIBLE);
                        texteDeChargement.setVisibility(View.INVISIBLE);
                    });
                    break;
                }
            }
        }
    }


}