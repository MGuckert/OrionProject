package com.example.tactigant20.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private Button BluetoothButton;
    private Button Appsettings;
    // private BluetoothAdapter mBlueAdapter;

    private static final String TAG_HOME = "DebugHomeFragment";

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
        // mBlueAdapter=BluetoothAdapter.getDefaultAdapter();

        //Bouton appsettings
        Appsettings = root.findViewById(R.id.settingsButton);
        Appsettings.setOnClickListener(BluetoothButtonListener);
        return root;
    }

    // Ce qu'il se passe quand on appuie sur le bouton principal
    private View.OnClickListener BluetoothButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case(R.id.settingsButton):
                    Log.d(TAG_HOME,"Appui sur le bouton autorisation settings");
                    showPermissionDialog();
                    break;
                case(R.id.bluetoothButton):
                    Log.d(TAG_HOME,"Appui sur le bouton Bluetooth");
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentOpenBluetoothSettings);
                    break;

            }
        }
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

    //méthode pour lancer app settings
    public void showPermissionDialog(){
        getActivity().getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}