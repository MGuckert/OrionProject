package com.example.tactigant20.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.tactigant20.R;
import com.example.tactigant20.ui.fragments.NotificationsFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfirmResetDialog extends Dialog {

    /**
     * Constructeur de l'objet <i>ConfirmResetDialog</i>
     *
     * @param context Contexte de l'application
     */
    public ConfirmResetDialog(Context context) {
        super(context);
    }

    /**
     * Méthode appelée lors de la création de la fenêtre pop-up.
     * Elle permet de définir le contenu de la fenêtre et d'attribuer des fonctions aux boutons
     *
     * @param savedInstanceState état de l'instance sauvegardée
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_reset);

        Button yesButton = this.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(this::cYesButton);
        Button noButton = this.findViewById(R.id.no_button);
        noButton.setOnClickListener(v -> this.dismiss());

    }

    /**
     * Fonction définissant l'action à réaliser lorsqu'on appuie sur le bouton de confirmation
     *
     * @param v le bouton en question
     */
    public void cYesButton(View v) {
        File file = new File(v.getContext().getFilesDir(), "vibration_modes_data.json");
        if (file.exists() && !file.isDirectory()) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(new JSONObject().toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NotificationsFragment.setDataReset(true);
        this.dismiss();
        Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.mode_reset), Toast.LENGTH_SHORT).show();
    }
}
