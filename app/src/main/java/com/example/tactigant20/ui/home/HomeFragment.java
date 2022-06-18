package com.example.tactigant20.ui.home;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
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
// import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
// import java.util.Queue;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private BluetoothGatt gatt;
    private TextView TextedeChargement;
    private static final String TAG_HOME = "DebugHomeFragment";
    private static final String TAG_HOME_BLE = "Bluetooth";

    // private Queue<Runnable> commandQueue;
    // private boolean commandQueueBusy;
    private ImageView ImageConfirmationConnection;
    private ImageView ImageConfirmationDeConnection;

    private String btn ="";
    private boolean ValeurDeConnexion=false;

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

        Log.d(TAG_HOME, "Appel de onCreate dans HomeFragment");
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Texte
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Boutons pour le Bluetooth

        // Lance le scan et se connecte à la carte si possible
        Button scanButton = root.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(BluetoothButtonListener);

        // Ouvre les paramètres Bluetooth
        Button bluetoothSettingsButton = root.findViewById(R.id.bluetoothSettingsButton);
        bluetoothSettingsButton.setOnClickListener(BluetoothButtonListener);

        // Lit les informations de la carte
        Button lectureButton = root.findViewById(R.id.lectureButton);
        lectureButton.setOnClickListener(BluetoothButtonListener);

        // Envoie des informations à la carte
        Button ecritureButton = root.findViewById(R.id.ecritureButton);
        ecritureButton.setOnClickListener(BluetoothButtonListener);

        // Se déconnecte de la carte
        Button deconnection = root.findViewById(R.id.deconnexionButton);
        deconnection.setOnClickListener(BluetoothButtonListener);

        // Texte de chargement
        TextedeChargement = root.findViewById(R.id.texteDeChargement);

        // Image de confirmation de connexion
        ImageConfirmationConnection = root.findViewById(R.id.connexionValide);

        // Image d'erreur/absence de connexion
        ImageConfirmationDeConnection= root.findViewById(R.id.connexionInvalide);

        return root;
    }

    // Ce qu'il se passe quand on appuie sur le bouton principal
    private final View.OnClickListener BluetoothButtonListener = new View.OnClickListener() {

        @SuppressLint("MissingPermission")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanButton:
                    Log.d(TAG_HOME_BLE, "Bouton pressé");
                    ImageConfirmationDeConnection.setVisibility(View.INVISIBLE);
                    TextedeChargement.setVisibility(View.VISIBLE);

                    adapter = BluetoothAdapter.getDefaultAdapter();
                    scanner = adapter.getBluetoothLeScanner();

                    if (scanner != null) {
                        String[] peripheralAddresses = new String[]{"94:3C:C6:06:CC:1E"};

                        // Liste des filtres
                        List<ScanFilter> filters = null;
                        if (peripheralAddresses != null) { // Toujours vrai ?
                            filters = new ArrayList<>();
                            for (String address : peripheralAddresses) {
                                ScanFilter filter = new ScanFilter.Builder()
                                        .setDeviceAddress(address)
                                        .build();
                                filters.add(filter);
                            }
                        }
                        // Paramètres de scan
                        ScanSettings scanSettings = new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                                .setReportDelay(0L)
                                .build();
                        scanner.startScan(filters, scanSettings, scanCallback);

                        Log.d(TAG_HOME_BLE, "Scan lancé");
                    }  else {
                        Log.e(TAG_HOME_BLE, "ERREUR : Impossible d'obtenir un scanner (onClick)");
                    }
                    break;
                case R.id.bluetoothSettingsButton:
                    Log.d(TAG_HOME, "Bouton paramètres Bluetooth pressé");
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentOpenBluetoothSettings);
                    break;
                case R.id.lectureButton:
                    Log.d(TAG_HOME, "Bouton lecture pressé");
                    if(ValeurDeConnexion) {
                        btn="Lecture";
                        gatt.discoverServices();
                    }
                    break;
                case R.id.ecritureButton:
                    Log.d(TAG_HOME, "Bouton ecriture pressé");
                    if(ValeurDeConnexion) {
                        btn="Ecriture";
                        gatt.discoverServices();
                    }
                    break;
                case R.id.deconnexionButton:
                    Log.d(TAG_HOME, "Bouton deconnexion pressé");
                    if(ValeurDeConnexion) {
                        gatt.disconnect();
                        scanner.stopScan(scanCallback);
                    }
                    break;
                default: break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        Log.d(TAG_HOME,"Appel de onDestroyView dans HomeFragment");
        super.onDestroyView();
        binding = null;
    }
/*
    public void alerter(String s) {
        Log.d(TAG_HOME,"Appel de alerter dans HomeFragment");
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }

 */

    // Obtention d'appareil BLE
    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d(TAG_HOME_BLE, device.getName());
            gatt = device.connectGatt(getContext(), true, bluetoothGattCallback, TRANSPORT_LE);
            Log.d(TAG_HOME_BLE, "Scan réussi");
        }

        // Inutile ?
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
            Log.d(TAG_HOME_BLE, "Appel de onBatchScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG_HOME_BLE, "ERREUR : scan (onScanFailed)");
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            if(status == GATT_SUCCESS) { // On s'est connecté à un appareil
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // We successfully connected, proceed with service discovery
                    ImageConfirmationDeConnection.setVisibility(View.INVISIBLE);
                    TextedeChargement.setVisibility(View.INVISIBLE);
                    ValeurDeConnexion=true;
                    ImageConfirmationConnection.setVisibility(View.VISIBLE);
                    Log.d(TAG_HOME_BLE, "CONNECTE");


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // We successfully disconnected on our own request
                    ValeurDeConnexion=false;
                    ImageConfirmationConnection.setVisibility(View.INVISIBLE);
                    ImageConfirmationDeConnection.setVisibility(View.VISIBLE);
                    gatt.close();
                    Log.d(TAG_HOME_BLE, "DECONNECTE");
                } else {
                    // Inutile ?
                }
            } else {
                Log.e(TAG_HOME_BLE, "ERREUR : pas de connexion établie (onConnectionStateChange)");
                gatt.close();
            }}

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                            // On parcourt l'ensemble des caractéristiques trouvées
                        if(btn.equals("Lecture")) {
                            Log.d(TAG_HOME_BLE, "On reçoit quelque chose de la carte !");
                            characteristic.getValue();
                            gatt.readCharacteristic(characteristic);
                        }
                        if (btn.equals("Ecriture")) {
                            Log.d(TAG_HOME_BLE, "On envoie quelque chose à la carte !");
                            characteristic.setValue("Allume"); // On envoie cette chaîne à la carte
                            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            gatt.writeCharacteristic(characteristic);
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
                Log.e(TAG_HOME_BLE, String.format(Locale.FRENCH,"ERREUR de lecture pour la caractéristique %s, status %d (onCharacteristicRead)", characteristic.getUuid(), status));
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
}