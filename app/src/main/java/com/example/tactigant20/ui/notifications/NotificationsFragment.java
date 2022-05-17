package com.example.tactigant20.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ListView notificationsList;

    private static final String TAG_NOTIFS = "DebugNotifsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_NOTIFS,"Appel de onCreate dans NotificationsFragment");

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<AppItem> appItemList = new ArrayList<>();
        appItemList.add(new AppItem("Appels", "call", 1));
        appItemList.add(new AppItem("Messages", "message", 1));
        notificationsList = root.findViewById(R.id.notificationsList);
        notificationsList.setAdapter(new AppItemAdapter(this.getContext(), appItemList));


        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG_NOTIFS,"Appel de onDestroyView dans NotificationsFragment");

        super.onDestroyView();
        binding = null;
    }
}