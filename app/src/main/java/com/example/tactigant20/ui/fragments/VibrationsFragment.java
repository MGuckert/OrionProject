package com.example.tactigant20.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentVibrationsBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class VibrationsFragment extends Fragment {

    private static final String TAG_VIBRAS = "debug_vibras_fragment";

    private ImageView modeVibrationImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        com.example.tactigant20.databinding.FragmentVibrationsBinding binding = FragmentVibrationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        modeVibrationImage = root.findViewById(R.id.modeVibrationImage);
        Glide.with(this)
                .load(R.drawable.mode1)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(modeVibrationImage); // Cas par défaut

        // Création des boutons
        Button mode1 = root.findViewById(R.id.buttonMode1);
        mode1.setOnClickListener(this::cMode1Button);
        Button mode2 = root.findViewById(R.id.buttonMode2);
        mode2.setOnClickListener(this::cMode2Button);
        Button mode3 = root.findViewById(R.id.buttonMode3);
        mode3.setOnClickListener(this::cMode3Button);

        return root;
    }

    private void cMode1Button(View v) {
        Log.d(TAG_VIBRAS, "Mode 1");
        Glide.with(this)
                .load(R.drawable.mode1)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(modeVibrationImage);
    }

    private void cMode2Button(View v) {
        Log.d(TAG_VIBRAS, "Mode 2");
        Glide.with(this)
                .load(R.drawable.mode2)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(modeVibrationImage);

    }

    private void cMode3Button(View v) {
        Log.d(TAG_VIBRAS, "Mode 3");
        Glide.with(this)
                .load(R.drawable.mode3)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(modeVibrationImage);
    }

}