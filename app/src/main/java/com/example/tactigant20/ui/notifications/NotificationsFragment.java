package com.example.tactigant20.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.databinding.FragmentNotificationsBinding;
import com.example.tactigant20.ui.home.HomeFragment;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private static final String TAG_NOTIFS = "DebugNotifsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_NOTIFS,"Appel de onCreate dans NotificationsFragment");

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    // Création d'un fragment (nécessaire pour scroll)
    public static NotificationsFragment newInstance(int page, String title) {
        Log.d(TAG_NOTIFS,"Appel de NotificationsFragment dans NotificationsFragment");
        NotificationsFragment fragmentMenu = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentMenu.setArguments(args);
        return fragmentMenu;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG_NOTIFS,"Appel de onDestroyView dans NotificationsFragment");

        super.onDestroyView();
        binding = null;
    }
}