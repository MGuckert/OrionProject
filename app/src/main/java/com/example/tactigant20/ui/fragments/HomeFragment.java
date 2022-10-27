package com.example.tactigant20.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG_HOME = "debug_home_fragment";

    private static CustomUIThread myHFCustomUIThread;

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


        myHFCustomUIThread = new CustomUIThread();
        myHFCustomUIThread.start();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cScanButton(View v) {
        Log.d(TAG_HOME, "Bouton Scan pressé");
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            MainActivity.getMyBLET().scan();
        } else {
            requestLocationPermission(v);
        }
    }

    private void requestLocationPermission(View v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(v, "Localisation requise",
                    Snackbar.LENGTH_INDEFINITE).setAction("ACCORDER", view -> ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            0)).show();

        }
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

    @SuppressWarnings({"BusyWait"})
    public class CustomUIThread extends Thread {

        private Boolean running = false;

        @Override
        public void run() {
            Log.d(TAG_HOME, "Lancement du thread");
            this.running = true;
            while (running) {
                //Log.d(TAG_HOME, "\nmValeurDeChargement : " + myBLET.getValeurDeChargement() +"\nmValeurDeConnection : " + myBLET.getValeurDeConnexion());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.getMyBLET() != null) {
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
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        imageConfirmationConnexion.setVisibility(View.INVISIBLE);
                        imageConfirmationDeconnexion.setVisibility(View.VISIBLE);
                        texteDeChargement.setVisibility(View.INVISIBLE);
                    });
                }
            }
        }

        public void setRunning(Boolean running) {
            this.running = running;
        }
    }

    public static CustomUIThread getMtHFCustomUIThread() {
            return myHFCustomUIThread;
    }

}