package com.example.tactigant20.ui.notifications;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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

    LayoutInflater layoutInflater;
    PackageManager packageManager;
    List<AppInfo> apps;
    public AppAdapter(Context context, List<AppInfo> apps) {
        super(context, R.layout.app_item_layout,apps);
        layoutInflater = LayoutInflater.from(context);
        packageManager = context.getPackageManager();
        this.apps = apps;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AppInfo current = apps.get(position);
        View view = convertView;

        if (view==null) {
            view = layoutInflater.inflate(R.layout.app_item_layout, parent, false);
        }

        TextView title = view.findViewById(R.id.titleTextView);
        title.setText(current.label);

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(current.info.packageName, 0);

            if (!TextUtils.isEmpty(current.info.packageName)) {
                TextView subtitle = view.findViewById(R.id.appSubtitle);
                subtitle.setText(current.info.packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ImageView iconImageView = view.findViewById(R.id.iconImage);
        Drawable icon = current.info.loadIcon(packageManager);

        iconImageView.setImageDrawable(icon);

        return view;
    }
}
