package com.example.tactigant20.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tactigant20.R;

import java.util.List;

/**
 * Classe qui permet de créer une liste d'applications à partir de la liste d'objets AppInfo et du layout d'un item app_item_layout.
 */
public class AppAdapter extends ArrayAdapter<AppInfo> {

    private final LayoutInflater mLayoutInflater;
    private final PackageManager mPackageManager;
    private final List<AppInfo> mApps;

    /**
     * Constructeur de l'objet AppAdapter.
     *
     * @param context le contexte de l'application
     * @param mApps la liste d'objets AppInfo à afficher
     */
    public AppAdapter(Context context, List<AppInfo> mApps) {
        super(context, R.layout.app_item_layout, mApps);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mPackageManager = context.getPackageManager();
        this.mApps = mApps;
    }

    /**
     * Fonction qui permet de créer une vue pour chaque élément de la liste d'objets AppInfo.
     *
     * @param position la position de l'élément dans la liste
     * @param convertView la vue à réutiliser, si disponible
     * @param parent le parent de la vue à créer
     *
     * @return la vue pour l'élément de la liste à la position spécifiée
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppInfo current = this.mApps.get(position);
        View v = convertView;

        if (v == null) {
            v = this.mLayoutInflater.inflate(R.layout.app_item_layout, parent, false);
        }

        TextView title = v.findViewById(R.id.titleTextView);
        String titre = current.getLabel();
        if (titre.length() > 12) {
            titre = titre.substring(0, 12) + "...";
        }
        title.setText(titre);
        TextView vibrationModeTextView = v.findViewById(R.id.vibrationModeTextView);
        if (current.getVibrationMode() == null)
            vibrationModeTextView.setText("N/A");
        else
            vibrationModeTextView.setText(current.getVibrationMode().getName());
        ImageView iconImageView = v.findViewById(R.id.iconImage);
        Drawable icon = current.getInfo().loadIcon(this.mPackageManager);

        iconImageView.setImageDrawable(icon);

        return v;
    }
}
