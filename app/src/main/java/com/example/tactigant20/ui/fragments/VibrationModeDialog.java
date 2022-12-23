package com.example.tactigant20.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.model.AppInfo;

import org.json.JSONException;

/**
 * Classe définissant un objet Dialog affichant une fenêtre pop-up permettant à l'utilisateur de choisir un mode de vibration pour une application donnée.
 * Cette fenêtre pop-up est affichée lorsque l'utilisateur clique sur un élément de la liste des applications dans le fragment Notifications.
 */
public class VibrationModeDialog extends Dialog {

    private final AppInfo mAppInfo;

    /**
     * Constructeur de l'objet VibrationModeDialog.
     *
     * @param context  Contexte de l'application
     * @param mAppInfo Objet de type AppInfo contenant les informations sur l'application pour laquelle le mode de vibration est à définir.
     */
    public VibrationModeDialog(Context context, AppInfo mAppInfo) {
        super(context);
        this.mAppInfo = mAppInfo;
    }

    /**
     * Méthode appelée lors de la création de la fenêtre pop-up.
     * Elle permet de définir le contenu de la fenêtre et d'attribuer un écouteur d'événement sur les boutons de sélection de mode de vibration.
     *
     * @param savedInstanceState État de l'instance sauvegardée
     */
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

    /**
     * Cette méthode gère le choix d'un mode de vibration par l'utilisateur dans la fenêtre pop-up.
     * Si l'utilisateur coche l'un des boutons radio, le mode de vibration de l'application associée est mis à jour et enregistré
     * dans le fichier de données JSON; la liste des applications est mise à jour et la fenêtre pop-up se ferme.
     *
     * @param v La vue du bouton radio qui a été cliqué
     */
    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        AppInfo currentItem = mAppInfo;
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
        NotificationsFragment.updateItem(currentItem);
        try {
            MainActivity.getMyVibrationsTool().saveVibrationMode(currentItem.getInfo().packageName, currentItem.getVibrationMode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationsFragment.getAdapter().notifyDataSetChanged();
        this.dismiss();
    }
}
