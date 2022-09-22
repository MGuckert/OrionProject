package com.example.tactigant20.ui.home;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.example.tactigant20.MyNotificationListenerService;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG_HOME = "debug_home_fragment";
    private static final String TAG_HOME_BLE = "debug_bluetooth";

    private FragmentHomeBinding binding;

    private TextView texteDeChargement;

    private ImageView imageConfirmationConnection;
    private ImageView imageConfirmationDeconnection;

    public static String Mode ="";

    private boolean valeurDeConnexion = false;
    private static BluetoothGatt gatt;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
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

        // On demande à l'utilisateur d'activer le Bluetooth si nécessaire
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});
        if (adapter == null || !adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult.launch(enableBtIntent);
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cScanButton(View v) {
        Log.d(TAG_HOME_BLE, "Bouton pressé");
        imageConfirmationDeconnection.setVisibility(View.INVISIBLE);
        texteDeChargement.setVisibility(View.VISIBLE);

        adapter = BluetoothAdapter.getDefaultAdapter();
        scanner = adapter.getBluetoothLeScanner();

        if (scanner != null) {
            String[] peripheralAddresses = new String[]{"94:3C:C6:06:CC:1E"}; // MAC du dispositif

            // Liste des filtres
            List<ScanFilter> filters;
            // Toujours vrai ?
            filters = new ArrayList<>();
            for (String address : peripheralAddresses) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceAddress(address)
                        .build();
                filters.add(filter);
            }
            // Paramètres de scan
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build();
            try {
                scanner.startScan(filters, scanSettings, scanCallback);
            } catch (SecurityException e) {
                Log.e(TAG_HOME_BLE, "SecurityException dans cScanButton");
            }

            Log.d(TAG_HOME_BLE, "Scan lancé");
        }  else {
            Log.e(TAG_HOME_BLE, "ERREUR : Impossible d'obtenir un scanner (onClick)");
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
        if(valeurDeConnexion) {
            try {
                gatt.disconnect();
                scanner.stopScan(scanCallback);
            } catch (SecurityException e) {
                Log.e(TAG_HOME_BLE, "SecurityException dans cDeconnectionButton");
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Obtention d'appareil BLE
    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            try {
                Log.d(TAG_HOME_BLE, "Obtention de l'appareil BLE " + device.getName());
                gatt = device.connectGatt(getContext(), true, bluetoothGattCallback, TRANSPORT_LE);
            } catch (SecurityException e) {
                Log.e(TAG_HOME_BLE, "SecurityException dans ScanCallBack");
            }
            Log.d(TAG_HOME_BLE, "Scan réussi");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG_HOME_BLE, "ERREUR : scan (onScanFailed)");
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            if(status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // On s'est connecté à un appareil
                    imageConfirmationDeconnection.setVisibility(View.INVISIBLE);
                    texteDeChargement.setVisibility(View.INVISIBLE);
                    valeurDeConnexion =true;
                    imageConfirmationConnection.setVisibility(View.VISIBLE);
                    Log.d(TAG_HOME_BLE, "CONNECTE");


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // On s'est déconnecté d'un appareil
                    valeurDeConnexion =false;
                    imageConfirmationConnection.setVisibility(View.INVISIBLE);
                    imageConfirmationDeconnection.setVisibility(View.VISIBLE);
                    try {
                        gatt.close();
                    } catch (SecurityException e) {
                        Log.e(TAG_HOME_BLE, "SecurityException dans ScanCallBack");
                    }
                    Log.d(TAG_HOME_BLE, "DECONNECTE");
                }

            } else {
                Log.e(TAG_HOME_BLE, "ERREUR : pas de connexion établie (onConnectionStateChange)");
                try {
                    gatt.close();
                } catch (SecurityException e) {
                    Log.e(TAG_HOME_BLE, "SecurityException dans ScanCallBack");
                }
            }}


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        // On parcourt l'ensemble des caractéristiques trouvées
                        if(Mode.equals("Lecture")) {
                            Log.d(TAG_HOME_BLE, "On reçoit quelque chose de la carte !");
                            characteristic.getValue();
                            try {
                                gatt.readCharacteristic(characteristic);
                            } catch (SecurityException e) {
                                Log.e(TAG_HOME_BLE, "SecurityException dans onServicesDiscovered");
                            }
                        }
                        if (Mode.equals("Ecriture")) {
                            switch(MyNotificationListenerService.vibrationMode) {
                                case "1":
                                Log.d(TAG_HOME_BLE, "On envoie quelque chose à la carte !");
                                characteristic.setValue("Allume"); // On envoie cette chaîne à la carte
                                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                try {
                                    gatt.writeCharacteristic(characteristic);
                                } catch (SecurityException e) {
                                    Log.e(TAG_HOME_BLE, "SecurityException dans onServicesDiscovered");
                                }
                                case "2":
                                case "3":
                                    break;
                            }
                        }
                    }
                }
            }
        }

        // Lecture d'informations depuis la carte
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            // Vérification que ça a fonctionné
            if (status != GATT_SUCCESS) {
                Log.e(TAG_HOME_BLE, String.format(Locale.FRENCH,"ERREUR de lecture pour la caractéristique : %s ; statut : %d (onCharacteristicRead)", characteristic.getUuid(), status));
                return;
            }

            // Traitement de la caractéristique pour la rendre lisible (byte -> String)
            byte[] value = characteristic.getValue();
            String s = new String(value, StandardCharsets.UTF_8);
            Log.d(TAG_HOME_BLE, "La carte nous envoie : \"" + s + "\"");

        }

        // Ecriture d'informations vers la carte
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG_HOME_BLE, "UUID de la caractéristique : " + characteristic.getUuid());
        }
    };

    public static BluetoothGatt getGatt() {
        return gatt;
    }

}