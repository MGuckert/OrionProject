package com.example.tactigant20.ui.vibrations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentVibrationsBinding;

public class VibrationsFragment extends Fragment implements View.OnClickListener {

    private FragmentVibrationsBinding binding;

    private ImageView modeVibrationImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VibrationsViewModel vibrationsViewModel =
                new ViewModelProvider(this).get(VibrationsViewModel.class);

        binding = FragmentVibrationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        modeVibrationImage = root.findViewById(R.id.modeVibrationImage);
        modeVibrationImage.setImageResource(R.drawable.wip_mode_1); // Cas par défaut

        // Création des boutons
        Button mode1 = (Button) root.findViewById(R.id.buttonMode1);
        mode1.setOnClickListener(this);
        Button mode2 = (Button) root.findViewById(R.id.buttonMode2);
        mode2.setOnClickListener(this);
        Button mode3 = (Button) root.findViewById(R.id.buttonMode3);
        mode3.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMode1:
                //Toast toast1 = Toast.makeText(getContext(), "Mode 1", Toast.LENGTH_SHORT);
                //toast1.show();
                modeVibrationImage.setImageResource(R.drawable.wip_mode_1);
                break;
            case R.id.buttonMode2:
                modeVibrationImage.setImageResource(R.drawable.wip_mode_2);
                break;
            case R.id.buttonMode3:
                modeVibrationImage.setImageResource(R.drawable.wip_mode_3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}