package com.example.tactigant20.ui.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import com.example.tactigant20.ui.home.HomeFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ListView appList;

    private static final String TAG_NOTIFS = "DebugNotifsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG_NOTIFS,"Appel de onCreate dans NotificationsFragment");

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appList = root.findViewById(R.id.appList);
        appList.setTextFilterEnabled(true);
//        LoadAppInfoTask task = new LoadAppInfoTask();
//        task.execute();
        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG_NOTIFS,"Appel de onDestroyView dans NotificationsFragment");

        super.onDestroyView();
        binding = null;
    }

    class LoadAppInfoTask extends AsyncTask<Integer,Integer, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("NewApi")
        @Override
        protected List<AppInfo> doInBackground(Integer... params) {

            List<AppInfo> apps = new ArrayList<>();
            PackageManager packageManager = getContext().getPackageManager();

            List<ApplicationInfo> infos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            System.err.println(infos.size());

            for (ApplicationInfo info:infos) {
                if (filter(info, packageManager)) {
                    AppInfo app = new AppInfo();
                    app.info = info;
                    app.label = (String) info.loadLabel(packageManager);
                    apps.add(app);
                }
            }

            Collections.sort(apps, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo appInfo1, AppInfo appInfo2) {
                    return appInfo1.label.compareTo(appInfo2.label);
                }
            });

            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            appList.setAdapter(new AppAdapter(getContext(),appInfos));
        }

        protected boolean filter(ApplicationInfo appInfo, PackageManager packageManager) {
            return (appInfo.packageName.equals("com.google.android.apps.docs") ||
                    appInfo.packageName.equals("com.google.android.gm") ||
                    appInfo.packageName.equals("com.google.android.googlequicksearchbox") ||
                    appInfo.packageName.equals("com.google.android.calendar") ||
                    appInfo.packageName.equals("com.google.android.chrome") ||
                    appInfo.packageName.equals("com.google.android.apps.deskclock") ||
                    appInfo.packageName.equals("com.google.android.apps.maps") ||
                    appInfo.packageName.equals("com.google.android.apps.messaging") ||
                    appInfo.packageName.equals("com.android.phone") ||
                    appInfo.packageName.equals("com.google.android.apps.photos") ||
                    appInfo.packageName.equals("com.google.android.apps.youtube") ||
                    appInfo.packageName.equals("com.google.android.apps.youtube.music") ||
                    ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM));
        }
    }
}