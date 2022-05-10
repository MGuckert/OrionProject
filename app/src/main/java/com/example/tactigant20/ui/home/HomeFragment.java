package com.example.tactigant20.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
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
    private FloatingActionButton BlueButton;
    private BluetoothAdapter mBlueAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        BlueButton=root.findViewById(R.id.bluetoothButton);
        BlueButton.setOnClickListener(BlueButtonListener);
        mBlueAdapter=BluetoothAdapter.getDefaultAdapter();
        return root;
    }
    private View.OnClickListener BlueButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(!mBlueAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,0);

            }
            if(mBlueAdapter.isEnabled()){
                mBlueAdapter.disable();
                alerter("Turning bluetooth OFF");

            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void alerter(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }
}