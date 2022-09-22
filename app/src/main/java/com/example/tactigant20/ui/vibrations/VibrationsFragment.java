package com.example.tactigant20.ui.vibrations;

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

public class VibrationsFragment extends Fragment {

    private FragmentVibrationsBinding binding;
    private ImageView modeVibrationImage;

    private static final String TAG_VIBRAS = "debug_vibras_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_VIBRAS,"Appel de onCreate dans VibrationsFragment");

        binding = FragmentVibrationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        modeVibrationImage = root.findViewById(R.id.modeVibrationImage);
        modeVibrationImage.setImageResource(R.drawable.wip_mode_1); // Cas par défaut

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
        // Toast toast1 = Toast.makeText(getContext(), "Mode 1", Toast.LENGTH_SHORT);
        // toast1.show();
        modeVibrationImage.setImageResource(R.drawable.wip_mode_1);
    }

    private void cMode2Button(View v) {
        modeVibrationImage.setImageResource(R.drawable.wip_mode_2);
    }

    private void cMode3Button(View v) {
        modeVibrationImage.setImageResource(R.drawable.wip_mode_3);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG_VIBRAS,"appel de onDestroyView dans VibrationsFragment");
        super.onDestroyView();
        binding = null;
    }
}