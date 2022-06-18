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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private Button BluetoothButton;
    private Button BluetoothSettingsButton;
    private Button LectureButton;
    private Button EcritureButton;
    private Button Deconnection;
    private BluetoothGatt gatt;
    private TextView TextedeChargement;
    private static final String TAG_HOME = "DebugHomeFragment";

    private Queue<Runnable> commandQueue;
    private boolean commandQueueBusy;
    private ImageView ImageConfirmationConnection;
    private ImageView ImageConfirmationDeConnection;

    private String btn ="";
    private boolean ValeurdeConnection=false;

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

        // Bouton bluetooth
        BluetoothButton = root.findViewById(R.id.bluetoothButton);
        BluetoothButton.setOnClickListener(BluetoothButtonListener);
        BluetoothSettingsButton = root.findViewById(R.id.ScanBouton);
        BluetoothSettingsButton.setOnClickListener(BluetoothButtonListener);
        LectureButton = root.findViewById(R.id.LectureBtn);
        LectureButton.setOnClickListener(BluetoothButtonListener);
        EcritureButton = root.findViewById(R.id.EcritureBtn);
        EcritureButton.setOnClickListener(BluetoothButtonListener);
        Deconnection = root.findViewById(R.id.btndeconnection);
        Deconnection.setOnClickListener(BluetoothButtonListener);
        // Texte de chargement
        TextedeChargement = root.findViewById(R.id.TextedeChargement);
        // Image de confirmation de connexion
        ImageConfirmationConnection = root.findViewById(R.id.connectionvalide);
        // Image d'erreur de connexion
        ImageConfirmationDeConnection= root.findViewById(R.id.connectioninvalide);

        return root;
    }

    // Ce qu'il se passe quand on appuie sur le bouton principal
    private View.OnClickListener BluetoothButtonListener = new View.OnClickListener() {

        @SuppressLint("MissingPermission")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ScanBouton:
                    Log.d("Bluetooth", "Bouton appuyé");
                    ImageConfirmationDeConnection.setVisibility(View.INVISIBLE);
                    TextedeChargement.setVisibility(View.VISIBLE);
                    adapter = BluetoothAdapter.getDefaultAdapter();
                    scanner = adapter.getBluetoothLeScanner();
                    if (scanner != null) {
                        String[] peripheralAddresses = new String[]{"94:3C:C6:06:CC:1E"};
                        // Build filters list
                        List<ScanFilter> filters = null;
                        if (peripheralAddresses != null) {
                            filters = new ArrayList<>();
                            for (String address : peripheralAddresses) {
                                ScanFilter filter = new ScanFilter.Builder()
                                        .setDeviceAddress(address)
                                        .build();
                                filters.add(filter);
                            }
                        }
                        ScanSettings scanSettings = new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                                .setReportDelay(0L)
                                .build();
                        scanner.startScan(filters, scanSettings, scanCallback);
                        Log.d("Bluetooth", "scan started");
                    }  else {
                        Log.e("Bluetooth", "could not get scanner object");
                    }
                    break;
                case R.id.bluetoothButton:
                    Log.d(TAG_HOME, "Appel de onClick dans HomeFragment");
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentOpenBluetoothSettings);
                    break;
                case R.id.LectureBtn:
                    if(ValeurdeConnection){
                        btn="Lecture";
                        gatt.discoverServices();
                    }
                    break;
                case R.id.EcritureBtn:
                    if(ValeurdeConnection){
                        btn="Ecriture";
                        gatt.discoverServices();
                    }
                    break;
                case R.id.btndeconnection:
                    if(ValeurdeConnection){
                        gatt.disconnect();
                        scanner.stopScan(scanCallback);
                    }
                    break;
                default: break;
            }
        };
    };

    @Override
    public void onDestroyView() {
        Log.d(TAG_HOME,"Appel de onDestroyView dans MenuFragment");
        super.onDestroyView();
        binding = null;
    }
    public void alerter(String s){
        Log.d(TAG_HOME,"Appel de alerter dans MenuFragment");
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d("Bluetooth", device.getName());
            // ...do whatever you want with this found device
            gatt = device.connectGatt(getContext(), true, bluetoothGattCallback, TRANSPORT_LE);
            Log.d("Bluetooth", "Scan réussi");
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
            Log.d("Bluetooth", "2");
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
            Log.d("Bluetooth", "Erreur Scan");
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            if(status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // We successfully connected, proceed with service discovery
                    ImageConfirmationDeConnection.setVisibility(View.INVISIBLE);
                    TextedeChargement.setVisibility(View.INVISIBLE);
                    ValeurdeConnection=true;
                    ImageConfirmationConnection.setVisibility(View.VISIBLE);
                    Log.d("Bluetooth", "CONNECTE");


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // We successfully disconnected on our own request
                    ValeurdeConnection=false;
                    ImageConfirmationConnection.setVisibility(View.INVISIBLE);
                    ImageConfirmationDeConnection.setVisibility(View.VISIBLE);
                    gatt.close();
                } else {
                    // We're CONNECTING or DISCONNECTING, ignore for now
                }
            } else {
                // An error happened...figure out what happened!
                gatt.close();
            }}
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        ///Once you have a characteristic object, you can perform read/write
                        //operations with it
                        if(btn=="Lecture") {
                            characteristic.getValue();
                            gatt.readCharacteristic(characteristic);
                        }
                        if (btn=="Ecriture"){
                            characteristic.setValue("Allume");
                            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            gatt.writeCharacteristic(characteristic);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            // Perform some checks on the status field
            if (status != GATT_SUCCESS) {
                Log.e("Bluetooth", String.format(Locale.ENGLISH,"ERROR: Read failed for characteristic: %s, status %d", characteristic.getUuid(), status));
                return;
            }
            // Characteristic has been read so processes it
            byte[] value = characteristic.getValue();
            String s = new String(value, StandardCharsets.UTF_8);
            Log.d("Bluetooth", s);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("Bluetooth", "Characteristic " + characteristic.getUuid() + " written");
        }
    };
}