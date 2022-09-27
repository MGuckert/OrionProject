package com.example.tactigant20.ui.home;

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

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;
import com.example.tactigant20.model.BluetoothLowEnergyTool;

public class HomeFragment extends Fragment {

    private static final String TAG_HOME = "debug_home_fragment";
    private static final String TAG_BLE = "debug_bluetooth";

    private TextView texteDeChargement;

    private ImageView imageConfirmationConnection;
    private ImageView imageConfirmationDeconnection;

    public static String Mode ="";


    private static BluetoothLowEnergyTool myBLET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
        imageConfirmationConnection = root.findViewById(R.id.connexionValide);

        // Image d'erreur/absence de connexion
        imageConfirmationDeconnection = root.findViewById(R.id.connexionInvalide);

        /*
        // On demande à l'utilisateur d'activer le Bluetooth si nécessaire
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});
        if (myBLET.getAdapter() == null || !myBLET.getAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult.launch(enableBtIntent);
        }

         */

         myBLET = new BluetoothLowEnergyTool(this.getContext(), "94:3C:C6:06:CC:1E");

        CustomUIThread myCustomUIThread = new CustomUIThread();
        myCustomUIThread.start();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cScanButton(View v) {
        Log.d(TAG_BLE, "Bouton pressé");
        myBLET.scan();
    }

    private void cBluetoothSettingsButton(View v) {
        Log.d(TAG_HOME, "Bouton paramètres Bluetooth pressé");
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    private void cDeconnectionButton(View v) {
        Log.d(TAG_HOME, "Bouton déconnexion pressé");
        myBLET.disconnect();
    }




    @SuppressWarnings("InfiniteLoopStatement")
    public class CustomUIThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG_HOME, "Lancement du thread");

            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (myBLET.getValeurDeChargement()) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        texteDeChargement.setVisibility(View.VISIBLE);
                        imageConfirmationDeconnection.setVisibility(View.INVISIBLE);
                        imageConfirmationDeconnection.setVisibility(View.INVISIBLE);
                    });
                } else {
                    if (myBLET.getValeurDeConnexion()) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            texteDeChargement.setVisibility(View.INVISIBLE);
                            imageConfirmationDeconnection.setVisibility(View.INVISIBLE);
                            imageConfirmationConnection.setVisibility(View.VISIBLE);
                        });

                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            texteDeChargement.setVisibility(View.INVISIBLE);
                            imageConfirmationConnection.setVisibility(View.INVISIBLE);
                            imageConfirmationDeconnection.setVisibility(View.VISIBLE);
                        });
                    }
                }
            }
        }
    }

    public static BluetoothLowEnergyTool getMyBLET() {
        return myBLET;
    }
}