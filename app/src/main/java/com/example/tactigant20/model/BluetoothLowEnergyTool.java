package com.example.tactigant20.model;

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
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.tactigant20.MyNotificationListenerService;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BluetoothLowEnergyTool {

    private static final String TAG_BLE = "debug_bluetooth";

    private final String mAdresseMAC;
    private final ScanCallback mScanCallback;
    private final BluetoothGattCallback mBluetoothGattCallback;

    private String mMode = "";
    private WeakReference<Context> mContext;
    private ValeurDeConnexion mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
    private BluetoothGatt mGatt;
    private BluetoothAdapter mAdapter;
    private BluetoothLeScanner mScanner;

    public BluetoothLowEnergyTool(String mAdresseMAC, Context mContext) {
        this.mAdresseMAC = mAdresseMAC;

        this.mContext = new WeakReference<>(mContext);

        this.mScanCallback = new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                try {
                    Log.d(TAG_BLE, "Obtention de l'appareil BLE : " + device.getName());
                    mGatt = device.connectGatt(mContext, true, mBluetoothGattCallback, TRANSPORT_LE);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                Log.d(TAG_BLE, "Scan réussi");
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG_BLE, "ERREUR de scan | code : "+ errorCode);
                mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
            }
        };

        this.mBluetoothGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                if(status == GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        // On s'est connecté à un appareil
                        mValeurDeConnexion = ValeurDeConnexion.CONNECTE;
                        Log.d(TAG_BLE, "CONNECTE");

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        // On s'est déconnecté d'un appareil
                        mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
                        try {
                            gatt.close();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG_BLE, "DECONNECTE");
                    }

                } else {
                    Log.e(TAG_BLE, "ERREUR : pas de connexion établie (onConnectionStateChange)");
                    try {
                        gatt.close();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } finally {
                        mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
                    }
                }}

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    List<BluetoothGattService> services = gatt.getServices();
                    for (BluetoothGattService service : services) {
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        for (BluetoothGattCharacteristic characteristic : characteristics) {
                            if(mMode.equals("Lecture")) {
                                Log.d(TAG_BLE, "On reçoit quelque chose de la carte !");
                                characteristic.getValue();
                                try {
                                    gatt.readCharacteristic(characteristic);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (mMode.equals("Ecriture")) {
                                switch(MyNotificationListenerService.getVibrationMode()) {
                                    case "1":
                                        Log.d(TAG_BLE, "On envoie quelque chose à la carte !");
                                        characteristic.setValue("Allume"); // On envoie cette chaîne à la carte
                                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                        try {
                                            gatt.writeCharacteristic(characteristic);
                                        } catch (SecurityException e) {
                                            e.printStackTrace();
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
                    Log.e(TAG_BLE, String.format(Locale.FRENCH,"ERREUR de lecture pour la caractéristique : %s ; statut : %d (onCharacteristicRead)", characteristic.getUuid(), status));
                    return;
                }
                // Traitement de la caractéristique pour la rendre lisible (byte -> String)
                byte[] value = characteristic.getValue();
                String s = new String(value, StandardCharsets.UTF_8);
                Log.d(TAG_BLE, "La carte nous envoie : \"" + s + "\"");

            }

            // Ecriture d'informations vers la carte
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG_BLE, "UUID de la caractéristique : " + characteristic.getUuid());
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scan() {
        if (!(mValeurDeConnexion == ValeurDeConnexion.CONNECTE)) {
            this.mAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mScanner = mAdapter.getBluetoothLeScanner();
            if (this.mScanner != null) {
                String[] peripheralAddresses = new String[]{this.mAdresseMAC}; // MAC du dispositif

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
                    this.mScanner.startScan(filters, scanSettings, mScanCallback);
                    this.mValeurDeConnexion = ValeurDeConnexion.CHARGEMENT;
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                Log.d(TAG_BLE, "Scan lancé");
            } else {
                Log.e(TAG_BLE, "ERREUR : Impossible d'obtenir un scanner");
            }
        } else {
            Log.w(TAG_BLE, "Veuillez vous déconnecter avant de relancer un scan");
        }
    }

    public void disconnect() {
        if (!(this.mValeurDeConnexion == ValeurDeConnexion.DECONNECTE)) {
            try {
                this.mGatt.disconnect();
                this.mScanner.stopScan(mScanCallback);
                mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public WeakReference<Context> getContext() {
        return this.mContext;
    }

    public ValeurDeConnexion getValeurDeConnexion() {
        return this.mValeurDeConnexion;
    }

    public BluetoothGatt getGatt() {
        return this.mGatt;
    }

    public BluetoothAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setContext(WeakReference<Context> mContext) {
        this.mContext = mContext;
    }

    public void setMode(String mMode) {
        this.mMode = mMode;
    }

    public enum ValeurDeConnexion {
        DECONNECTE, CHARGEMENT, CONNECTE
    }
}

