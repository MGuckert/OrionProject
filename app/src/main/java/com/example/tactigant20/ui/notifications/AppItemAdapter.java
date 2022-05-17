package com.example.tactigant20.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tactigant20.R;
import com.example.tactigant20.ui.vibrations.VibrationsViewModel;

import org.w3c.dom.Text;

import java.util.List;

public class AppItemAdapter extends BaseAdapter {

    private Context context;
    private List<AppItem> appItemList;
    private LayoutInflater inflater;

    public AppItemAdapter(Context context, List<AppItem> appItemList) {
        this.context = context;
        this.appItemList = appItemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appItemList.size();
    }

    @Override
    public AppItem getItem(int i) {
        return appItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.adapter_item,null);
        AppItem currentItem = appItemList.get(i);
        String name = currentItem.getName();
        int mode = currentItem.getVibrationMode();

        ImageView itemIcon = view.findViewById(R.id.item_icon);
        switch (currentItem.getMnemonic()) {
            case "call":
                itemIcon.setImageResource(R.drawable.ic_call);
                break;
            case "message":
                itemIcon.setImageResource(R.drawable.ic_message);
                break;
            default:
                break;
        }

        TextView itemName = view.findViewById(R.id.item_name);
        itemName.setText(name);

        TextView itemVibrationMode = view.findViewById(R.id.item_vibration_mode);
        String vibrationMode = "Mode " + mode;
        itemVibrationMode.setText(vibrationMode);

        return view;
    }
}
