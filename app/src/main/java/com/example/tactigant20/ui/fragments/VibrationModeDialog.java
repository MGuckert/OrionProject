package com.example.tactigant20.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.model.AppInfo;

public class VibrationModeDialog extends Dialog {

    private final AppInfo mAppInfo;

    public VibrationModeDialog(Context context, AppInfo mAppInfo) {
        super(context);
        this.mAppInfo = mAppInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vibration_mode);

        // Boutons du dialog de notifications
        RadioGroup vibrationModeRadioGroup = findViewById(R.id.vibrationModeRadioGroup);
        RadioButton NARadioButton = findViewById(R.id.radioButtonNA);
        NARadioButton.setOnClickListener(this::onRadioButtonClicked);
        RadioButton Mode1RadioButton = findViewById(R.id.radioButtonMode1);
        Mode1RadioButton.setOnClickListener(this::onRadioButtonClicked);
        RadioButton Mode2RadioButton = findViewById(R.id.radioButtonMode2);
        Mode2RadioButton.setOnClickListener(this::onRadioButtonClicked);
        RadioButton Mode3RadioButton = findViewById(R.id.radioButtonMode3);
        Mode3RadioButton.setOnClickListener(this::onRadioButtonClicked);

        switch (this.mAppInfo.getVibrationMode()) {
            case "N":
                vibrationModeRadioGroup.check(R.id.radioButtonNA);
                break;
            case "1":
                vibrationModeRadioGroup.check(R.id.radioButtonMode1);
                break;
            case "2":
                vibrationModeRadioGroup.check(R.id.radioButtonMode2);
                break;
            case "3":
                vibrationModeRadioGroup.check(R.id.radioButtonMode3);
                break;
        }

    }

    //Fonction qui gère le choix d'un mode de vibration dans la fenêtre pop-up du fragment notifications
    public void onRadioButtonClicked(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();
        // Check which radio button was clicked
        AppInfo currentItem = NotificationsFragment.getCurrentItem();
        int boutonRadio = v.getId();
        if (boutonRadio == R.id.radioButtonNA) {
            if (checked) {
                currentItem.setVibrationMode("N");
            }
        }
        if (boutonRadio == R.id.radioButtonMode1) {
            if (checked) {
                currentItem.setVibrationMode("1");
            }
        }
        if (boutonRadio == R.id.radioButtonMode2) {
            if (checked) {
                currentItem.setVibrationMode("2");
            }
        }
        if (boutonRadio == R.id.radioButtonMode3) {
            if (checked) {
                currentItem.setVibrationMode("3");
            }
        }
        Log.e("Modes de vibration", "Mode de vibration choisi : " + currentItem.getVibrationMode());
        NotificationsFragment.setFromIndex(currentItem);
        MainActivity.getMyVibrationsTool().saveVibrationMode(currentItem.getInfo().packageName, currentItem.getVibrationMode());
        NotificationsFragment.getAdapter().notifyDataSetChanged();
        this.dismiss();
    }

}
