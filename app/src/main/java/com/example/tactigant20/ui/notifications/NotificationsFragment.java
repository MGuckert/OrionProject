package com.example.tactigant20.ui.notifications;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private List<AppInfo> appList;
    private ListView appListView;
    private int currentItemPosition;
    private AppAdapter adapter;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RadioGroup vibrationModeRadioGroup;
    private TextView descriptionTextView;

    private static final String TAG_NOTIFS = "DebugNotifsFragment";

    public int getCurrentItemPosition() {
        return currentItemPosition;
    }

    public AppInfo getCurrentItem() {
        return appList.get(currentItemPosition);
    }

    public AppAdapter getAdapter() {
        return adapter;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setFromIndex(int position, AppInfo appInfo) {
        appList.set(position, appInfo);
    }

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

        appListView = root.findViewById(R.id.appList);
        appListView.setTextFilterEnabled(true);
        appList = new ArrayList<>();
        LoadAppInfoTask task = new LoadAppInfoTask();
        task.execute();

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.err.println(i);
                currentItemPosition = i;
                AppInfo currentItem = (AppInfo) appListView.getItemAtPosition(currentItemPosition);
                System.err.println(currentItem.label);
                createNewVibrationModeDialog(currentItem);
//                TextView vibrationModeTextView = (TextView) root.findViewById(R.id.vibrationModeTextView);
//                if (currentItem.vibrationMode.equals("NA"))
//                    vibrationModeTextView.setText("N/A");
//                else
//                    vibrationModeTextView.setText(String.format("Mode %s", currentItem.vibrationMode));
            }
        });
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

            PackageManager packageManager = getContext().getPackageManager();

            List<ApplicationInfo> infos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            System.err.println(infos.size());

            for (ApplicationInfo info:infos) {
                if (filter(info, packageManager)) {
                    AppInfo app = new AppInfo();
                    app.info = info;
                    app.label = (String) info.loadLabel(packageManager);
                    app.vibrationMode = "NA";
                    appList.add(app);
                }
            }

            Collections.sort(appList, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo appInfo1, AppInfo appInfo2) {
                    return appInfo1.label.compareTo(appInfo2.label);
                }
            });

            return appList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            adapter = new AppAdapter(getContext(),appInfos);
            appListView.setAdapter(adapter);
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

    public void createNewVibrationModeDialog(AppInfo appInfo) {
        dialogBuilder = new AlertDialog.Builder(this.getContext());
        final View vibrationModeDialog = getLayoutInflater().inflate(R.layout.vibration_popup_menu, null);
        vibrationModeRadioGroup = vibrationModeDialog.findViewById(R.id.vibrationModeRadioGroup);
        switch (appInfo.vibrationMode) {
            case "NA":
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
        descriptionTextView = vibrationModeDialog.findViewById(R.id.descriptionTextView);
        dialogBuilder.setView(vibrationModeDialog);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}