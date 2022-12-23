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

/**
 * Une connexion BLE sous forme d'objet
 * Cet objet permet de contrôler une connexion BLE pendant l'ensemble de son cycle de vie en gérant l'appairage et la déconnexion, mais aussi l'échange de données entre un serveur et un client
 *
 * @author Thibaud P., Roman T.
 * @since 1.0
 */
public class BluetoothLowEnergyTool {

    private static final String TAG_BLE = "debug_bluetooth";
    private final ScanCallback mScanCallback;
    private final BluetoothGattCallback mBluetoothGattCallback;
    private final WeakReference<Context> mContext;
    private String mAdresseMAC;
    private String mMode = "";
    private ValeurDeConnexion mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
    private BluetoothGatt mGatt;
    private BluetoothAdapter mAdapter;
    private BluetoothLeScanner mScanner;

    /**
     * Constructeur unique de <i>BluetoothLowEnergyTool</i>
     *
     * @param mAdresseMAC l'adresse MAC de l'objet auquel on souhaite se connecter
     * @param mContext    le contexte dans lequel le <i>BluetoothLowEnergyTool</i> est instancié
     */
    public BluetoothLowEnergyTool(String mAdresseMAC, Context mContext) {
        this.mAdresseMAC = mAdresseMAC;

        this.mContext = new WeakReference<>(mContext);

        this.mScanCallback = new ScanCallback() {
            /**
             * Fonction appelée lorsqu'un scan réussit
             * @param callbackType information sur la manière dont la fonction a été appelée
             * @param result un objet correspondant à l'appareil trouvé
             * @see <a href="https://developer.android.com/reference/android/bluetooth/le/ScanCallback#onScanResult(int,%20android.bluetooth.le.ScanResult)">Plus d'informations</a>
             */
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

            /**
             * Fonction appelée lorsqu'un scan échoue
             * @param errorCode information sur l'erreur
             * @see <a href="https://developer.android.com/reference/android/bluetooth/le/ScanCallback#onScanFailed(int)">Plus d'informations</a>
             */
            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG_BLE, "ERREUR de scan | code : " + errorCode);
                mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
            }
        };

        this.mBluetoothGattCallback = new BluetoothGattCallback() {
            /**
             * Fonction appelée à chaque connexion/déconnexion
             * @param gatt le client GATT
             * @param status le statut de l'opération
             * @param newState le nouvel état (connecté/déconnecté)
             * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt,%20int,%20int)">Plus d'informations</a>
             */
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                if (status == GATT_SUCCESS) {
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
                }
            }

            /**
             * Fonction appelée à chaque tentative d'échange de données via BLE
             * @param gatt le client GATT concerné
             * @param status indique si l'opération a réussi
             * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback#onServicesDiscovered(android.bluetooth.BluetoothGatt,%20int)">Plus d'informations</a>
             */
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    List<BluetoothGattService> services = gatt.getServices();
                    for (BluetoothGattService service : services) {
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        for (BluetoothGattCharacteristic characteristic : characteristics) {
                            if (mMode.equals("Lecture")) {
                                Log.d(TAG_BLE, "On reçoit quelque chose de la carte !");
                                characteristic.getValue();
                                try {
                                    gatt.readCharacteristic(characteristic);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (mMode.equals("Ecriture")) {
                                switch (MyNotificationListenerService.getVibrationMode()) {
                                    case "1":
                                        sendData(gatt, characteristic, "Allume 1");
                                    case "2":
                                        sendData(gatt, characteristic, "Allume 2");
                                    case "3":
                                        sendData(gatt, characteristic, "Allume 3");
                                        break;
                                }
                            }
                        }
                    }
                }
            }

            /**
             * Fonction appelée à chaque lecture d'informations depuis l'objet connecté
             * @param gatt le client GATT concerné
             * @param characteristic la <i>characteristic</i> reçue
             * @param status indique si l'opération a réussi
             * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt,%20android.bluetooth.BluetoothGattCharacteristic,%20int)">Plus d'informations</a>
             */
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
                // Vérification que ça a fonctionné
                if (status != GATT_SUCCESS) {
                    Log.e(TAG_BLE, String.format(Locale.FRENCH, "ERREUR de lecture pour la caractéristique : %s ; statut : %d (onCharacteristicRead)", characteristic.getUuid(), status));
                    return;
                }
                // Traitement de la caractéristique pour la rendre lisible (byte -> String)
                byte[] value = characteristic.getValue();
                String s = new String(value, StandardCharsets.UTF_8);
                Log.d(TAG_BLE, "La carte nous envoie : \"" + s + "\"");

            }

            /**
             * Fonction appelée à chaque envoi de données vers l'objet connecté
             * @param gatt le client GATT concerné
             * @param characteristic la <i>characteristic</i> envoyée
             * @param status indique si l'opération a réussi
             * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback#onCharacteristicWrite(android.bluetooth.BluetoothGatt,%20android.bluetooth.BluetoothGattCharacteristic,%20int)">Plus d'informations</a>
             */
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG_BLE, "UUID de la caractéristique : " + characteristic.getUuid());
            }
        };
    }

    /**
     * Quand cette fonction est appelée, le téléphone de l'utilisateur tente de s'appairer avec l'objet portant l'adresse MAC <i>mAdresseMAC</i>
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scan() {
        if (mValeurDeConnexion == ValeurDeConnexion.DECONNECTE) {

            this.mAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mScanner = mAdapter.getBluetoothLeScanner();
            if (this.mScanner != null) {
                String[] peripheralAddresses = new String[]{this.mAdresseMAC}; // MAC du dispositif

                // Liste des filtres
                List<ScanFilter> filters;
                // Toujours vrai ?
                filters = new ArrayList<>();
                for (String address : peripheralAddresses) {
                    ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(address).build();
                    filters.add(filter);
                }
                // Paramètres de scan
                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT).setReportDelay(0L).build();
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

    /**
     * Quand cette fonction est appelée, le téléphone de l'utilisateur coupe toute connexion BLE existante
     */
    public void disconnect() {
        try {
            this.mScanner.stopScan(mScanCallback);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (this.mGatt != null) {
            try {
                this.mGatt.disconnect();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        this.mValeurDeConnexion = ValeurDeConnexion.DECONNECTE;
    }

    /**
     * Fonction centralisant l'envoi d'ordres à un appareil BLE
     *
     * @param gatt           le client GATT concerné
     * @param characteristic la <i>characteristic</i> qui porte le message
     * @param message        la chaîne de caractères à envoyer à la carte
     */
    public void sendData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String message) {
        Log.d(TAG_BLE, "On envoie quelque chose à la carte !");
        characteristic.setValue(message); // On envoie cette chaîne à la carte
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        try {
            gatt.writeCharacteristic(characteristic);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter pour le contexte d'instanciation du <i>BluetoothLowEnergyTool</i>
     *
     * @return le contexte dans lequel cet objet a été instancié
     */
    public WeakReference<Context> getContext() {
        return this.mContext;
    }

    /**
     * Getter pour l'état de la connexion
     *
     * @return l'état de la connexion (CONNECTÉ/DÉCONNECTÉ/CHARGEMENT)
     */
    public ValeurDeConnexion getValeurDeConnexion() {
        return this.mValeurDeConnexion;
    }

    /**
     * Getter pour le GATT
     *
     * @return le GATT utilisé par le <i>BluetoothLowEnergyTool</i>
     */
    public BluetoothGatt getGatt() {
        return this.mGatt;
    }

    /**
     * Getter pour l'<i>Adapter</i>
     * L'état de l'<i>Adapter</i> est une information cruciale sur le cycle de vie de la connexion BLE
     *
     * @return l'<i>Adapter</i> utilisé par le <i>BluetoothLowEnergyTool</i>
     */
    public BluetoothAdapter getAdapter() {
        return this.mAdapter;
    }

    /**
     * Setter pour l'adresse MAC
     * Permet de changer l'objet auquel on souhaite se connecter
     *
     * @param mAdresseMAC l'adresse MAC de l'objet auquel on souhaite se connecter par BLE
     */
    public void setAdresseMAC(String mAdresseMAC) {
        this.mAdresseMAC = mAdresseMAC;
    }

    /**
     * Setter pour le mode d'échange de données
     *
     * @param mMode le mode d'échange de données ("Lecture"/"Ecriture")
     */
    public void setMode(String mMode) {
        this.mMode = mMode;
    }

    public enum ValeurDeConnexion {
        DECONNECTE, CHARGEMENT, CONNECTE
    }
}