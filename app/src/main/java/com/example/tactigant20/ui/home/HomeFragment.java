package com.example.tactigant20.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private Button BluetoothButton;
    private Button BluetoothSettingsButton;
    private TextView TextedeChargement;

    private static final String TAG_HOME = "DebugHomeFragment";
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {

        Log.d(TAG_HOME,"Appel de onCreate dans HomeFragment");
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Texte
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Bouton bluetooth
        BluetoothButton=root.findViewById(R.id.bluetoothButton);
        BluetoothButton.setOnClickListener(BluetoothButtonListener);
        BluetoothSettingsButton=root.findViewById(R.id.ScanBouton);
        BluetoothSettingsButton.setOnClickListener(BluetoothButtonListener);

        // Texte de chargement
        TextedeChargement = root.findViewById(R.id.TextedeChargement);
        return root;
    }

    // Ce qu'il se passe quand on appuie sur le bouton principal
    private View.OnClickListener BluetoothButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ScanBouton:
                    TextedeChargement.setVisibility(View.VISIBLE);
                    if(!adapter.isEnabled()){
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,0);
                    }
                    // Filtre
                        if (scanner != null) {
                            UUID BLP_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb"); // A changer en fonction de celui de la carte
                            UUID[] serviceUUIDs = new UUID[]{BLP_SERVICE_UUID};
                            List<ScanFilter> filters = null;
                            if(serviceUUIDs != null) {
                                filters = new ArrayList<>();
                                for (UUID serviceUUID : serviceUUIDs) {
                                    ScanFilter filter = new ScanFilter.Builder()
                                            .setServiceUuid(new ParcelUuid(serviceUUID))
                                            .build();
                                    filters.add(filter);
                                }
                            }
                        //Type de scan
                            ScanSettings scanSettings = new ScanSettings.Builder()
                                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                                    .setReportDelay(0L)
                                    .build();
                        //scanner.startScan(filters, scanSettings, scanCallback);
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
            TextedeChargement.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }
    };
}