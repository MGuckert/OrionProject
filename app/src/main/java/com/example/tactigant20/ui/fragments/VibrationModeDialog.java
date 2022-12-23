package com.example.tactigant20.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.model.AppInfo;
import com.example.tactigant20.model.VibrationMode;

import org.json.JSONException;

import java.util.List;


/**
 * Classe définissant un objet Dialog affichant une fenêtre pop-up permettant à l'utilisateur de choisir un mode de vibration pour une application donnée.
 * Cette fenêtre pop-up est affichée lorsque l'utilisateur clique sur un élément de la liste des applications dans le fragment Notifications.
 */
public class VibrationModeDialog extends Dialog {

    private final AppInfo mAppInfo;
    private VibrationMode selectedVibrationMode;

    /**
     * Constructeur de l'objet <i>VibrationModeDialog</i>
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
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vibration_mode);

        TextView vibrationModeChoiceApp = findViewById(R.id.vibration_mode_choice_app);
        vibrationModeChoiceApp.setText("Affectez un mode de vibration à " + mAppInfo.getLabel() + " : ");
        //Initialisation du spinner contenant les modes de vibrations
        Spinner vibrationModeSpinner = findViewById(R.id.vibrationModeSpinner);
        List<VibrationMode> vibrationModes = VibrationMode.getSavedVibrationModes(getContext());
        vibrationModes.add(0,new VibrationMode("N/A",""));
        ArrayAdapter<VibrationMode> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, vibrationModes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        vibrationModeSpinner.setAdapter(adapter);

        VibrationMode currentVibrationMode = mAppInfo.getVibrationMode();
        vibrationModeSpinner.setSelection(adapter.getPosition(currentVibrationMode));
        selectedVibrationMode = currentVibrationMode;

        vibrationModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVibrationMode = vibrationModes.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedVibrationMode = VibrationMode.getDefaultVibrationMode();
            }
        });

        Button vibrationDialogOKButton = findViewById(R.id.vibrationDialogOKButton);
        vibrationDialogOKButton.setOnClickListener(view -> {
            mAppInfo.setVibrationMode(selectedVibrationMode);
            NotificationsFragment.updateItem(mAppInfo);
            try {
                MainActivity.getMyVibrationsTool().saveAppVibrationModeId(mAppInfo.getInfo().packageName, mAppInfo.getVibrationMode().getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NotificationsFragment.getAdapter().notifyDataSetChanged();
            dismiss();
        });
    }
}
