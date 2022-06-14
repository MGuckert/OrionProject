package com.example.tactigant20.ui.home;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private Button BluetoothButton;
    private Button BluetoothSettingsButton;
    private TextView TextedeChargement;
    private static final String TAG_HOME = "DebugHomeFragment";


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

        // Texte de chargement
        TextedeChargement = root.findViewById(R.id.TextedeChargement);
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
                    TextedeChargement.setVisibility(View.VISIBLE);

                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

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
            // ...do whatever you want with this found device
            BluetoothGatt gatt = device.connectGatt(getContext(), true, bluetoothGattCallback, TRANSPORT_LE);
            Log.d("Bluetooth", "1");
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
            Log.d("Bluetooth", "2");
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
            Log.d("Bluetooth", "3");
        }
    };
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        public void  onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value, int status){
            Log.d("Bluetooth", "Connecté");
        }
    };
}