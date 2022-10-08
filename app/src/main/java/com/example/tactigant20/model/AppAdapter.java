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


public class AppAdapter extends ArrayAdapter<AppInfo> {

    private final LayoutInflater mLayoutInflater;
    private final PackageManager mPackageManager;
    private final List<AppInfo> mApps;

    public AppAdapter(Context context, List<AppInfo> mApps) {
        super(context, R.layout.app_item_layout, mApps);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mPackageManager = context.getPackageManager();
        this.mApps = mApps;
    }

    @NonNull
    @Override
    //Fonction permettant de former la liste d'applications à partir de la liste d'AppInfos et du layout d'un item app_item_layout
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppInfo current = this.mApps.get(position);
        View v = convertView;

        if (v==null) {
            v = this.mLayoutInflater.inflate(R.layout.app_item_layout, parent, false);
        }

        TextView title = v.findViewById(R.id.titleTextView);
        title.setText(current.getLabel());
        TextView vibrationModeTextView = v.findViewById(R.id.vibrationModeTextView);
        if (current.getVibrationMode().equals("N"))
            vibrationModeTextView.setText("N/A");
        else
            vibrationModeTextView.setText(String.format("Mode %s", current.getVibrationMode()));

        ImageView iconImageView = v.findViewById(R.id.iconImage);
        Drawable icon = current.getInfo().loadIcon(this.mPackageManager);

        iconImageView.setImageDrawable(icon);

        return v;
    }
}
