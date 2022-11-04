package com.example.tactigant20.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG_HOME = "debug_home_fragment";

    private static CustomUIThread myHFCustomUIThread;

    private ImageView imageConfirmationConnexion;
    private ImageView imageConfirmationDeconnexion;
    private TextView texteDeChargement;
    private Button connectionButton;

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
        // OU se déconnecte
        connectionButton = root.findViewById(R.id.connectionButton);
        connectionButton.setOnClickListener(this::cConnectionButton);
        // Ouvre les paramètres Bluetooth
        Button bluetoothSettingsButton = root.findViewById(R.id.bluetoothSettingsButton);
        bluetoothSettingsButton.setOnClickListener(this::cBluetoothSettingsButton);

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
    private void cConnectionButton(View v) {
        if (connectionButton.getText().equals(requireContext().getResources().getString(R.string.connection))) {
            Log.d(TAG_HOME, "Bouton Scan pressé");
            if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG_HOME, "Besoin d'activer la localisation");
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle("Information");
                builder.setMessage(requireContext().getResources().getString(R.string.textePermLoc));
                builder.setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0));
                builder.setNegativeButton("Non", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            MainActivity.getMyBLET().scan();
        } else if (connectionButton.getText().equals(requireContext().getResources().getString(R.string.disconnection))) {
            Log.d(TAG_HOME, "Bouton déconnexion pressé");
            MainActivity.getMyBLET().disconnect();
        } else {
            Log.e(TAG_HOME, "Etat de connection inconnu");
        }

    }

    private void cBluetoothSettingsButton(View v) {
        Log.d(TAG_HOME, "Bouton paramètres Bluetooth pressé");
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    @SuppressWarnings({"BusyWait"})
    public class CustomUIThread extends Thread {
        private Boolean running = false;

        @Override
        public void run() {
            Log.d(TAG_HOME, "Lancement du thread dans HomeFragment");
            this.running = true;
            while (running) {
                try {
                    requireActivity().runOnUiThread(() -> {
                        if (MainActivity.getMyBLET() != null) {
                            switch (MainActivity.getMyBLET().getValeurDeConnexion()) {
                                case DECONNECTE:
                                    UIUpdate(R.string.connection, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                                    break;
                                case CHARGEMENT:
                                    UIUpdate(R.string.disconnection, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                                    break;
                                case CONNECTE:
                                    UIUpdate(R.string.disconnection, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                                    break;
                            }
                        } else {
                            UIUpdate(R.string.connection, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void UIUpdate(int EtatdeConnexion, int visibiliteICC, int visibiliteICD, int visibiliteTDC) {
            connectionButton.setText(EtatdeConnexion);
            imageConfirmationConnexion.setVisibility(visibiliteICC);
            imageConfirmationDeconnexion.setVisibility(visibiliteICD);
            texteDeChargement.setVisibility(visibiliteTDC);
        }

        public void setRunning(Boolean running) {
            this.running = running;
        }
        }

        public static CustomUIThread getMyHFCustomUIThread() {
            return myHFCustomUIThread;
        }

}