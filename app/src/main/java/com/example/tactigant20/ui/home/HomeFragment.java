package com.example.tactigant20.ui.home;

import android.bluetooth.BluetoothAdapter;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;
import com.example.tactigant20.model.BluetoothLowEnergyTool;

public class HomeFragment extends Fragment {

    private static final String TAG_HOME = "debug_home_fragment";

    private TextView texteDeChargement;

    private ImageView imageConfirmationConnection;
    private ImageView imageConfirmationDeconnection;

    public static String Mode ="";

    private static BluetoothLowEnergyTool myBLET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myBLET = new BluetoothLowEnergyTool("94:3C:C6:06:CC:1E", this.getContext());

        // On demande à l'utilisateur d'activer le Bluetooth si nécessaire
        if (myBLET.getAdapter() == null || !myBLET.getAdapter().isEnabled()) {
            ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {});
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult.launch(enableBtIntent);
        }

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


        CustomUIThread myCustomUIThread = new CustomUIThread();
        myCustomUIThread.start();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cScanButton(View v) {
        Log.d(TAG_HOME, "Bouton Scan pressé");
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

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    public class CustomUIThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG_HOME, "Lancement du thread");

            while(true) {
                Log.d(TAG_HOME, "\nmValeurDeChargement : " + myBLET.getValeurDeChargement() +"\nmValeurDeConnection : " + myBLET.getValeurDeConnexion());
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