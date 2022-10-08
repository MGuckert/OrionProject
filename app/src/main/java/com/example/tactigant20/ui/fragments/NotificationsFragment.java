package com.example.tactigant20.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentNotificationsBinding;
import com.example.tactigant20.model.AppAdapter;
import com.example.tactigant20.model.AppInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG_NOTIFS = "debug_notifs_fragment";

    private List<AppInfo> appList;
    private ListView appListView;
    private int currentItemPosition;
    private AppAdapter adapter;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        com.example.tactigant20.databinding.FragmentNotificationsBinding binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appListView = root.findViewById(R.id.appList);
        appListView.setTextFilterEnabled(true);
        appList = new ArrayList<>();
        LoadAppInfoTask task = new LoadAppInfoTask();
        task.execute();

        appListView.setOnItemClickListener((adapterView, view, i, l) -> {
            System.err.println(i);
            currentItemPosition = i;
            AppInfo currentItem = (AppInfo) appListView.getItemAtPosition(currentItemPosition);
            System.err.println(currentItem.getLabel());
            createNewVibrationModeDialog(currentItem);
        });
        return root;
    }

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

    //Classe permettant de générer la liste des applications dans un thread auxiliaire (en arrière-plan)
    @SuppressWarnings({"StaticFieldLeak", "deprecation"})
    class LoadAppInfoTask extends AsyncTask<Integer,Integer, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected List<AppInfo> doInBackground(Integer... params) {

            PackageManager packageManager = requireContext().getPackageManager();

            List<ApplicationInfo> infos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            System.err.println(infos.size());
            File vibration_modes_data = new File(requireContext().getFilesDir(),"vibration_modes_data.txt");
            if (!vibration_modes_data.exists()) {
                try {
                    requireContext().openFileOutput("vibration_modes_data.txt",MODE_PRIVATE);
                } catch (FileNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            for (ApplicationInfo info:infos) {
                if (filter(info)) {
                    AppInfo app = new AppInfo();
                    app.setInfo(info);
                    app.setLabel((String) info.loadLabel(packageManager));
                    //On cherche si le fichier "vibration_modes_data.txt" existe : si c'est le cas, alors on essaie de lire les données
                    //Sinon, on affecte aucun ("N/A", correspondant à "N" dans le code) mode de vibration à l'application
                    //On lit les données du fichier pour trouver l'application correspondante
                    if (getContext() == null) {
                        Log.e(TAG_NOTIFS, "getContext() renvoie null dans NotificationsFragment");
                    } else {
                        String mode = MainActivity.getMyVibrationsTool().loadVibrationMode(app.getInfo().packageName, getContext());
                        if (mode.equals("UNKNOWN"))
                            app.setVibrationMode("N");
                        else
                            app.setVibrationMode(mode);
                        appList.add(app);
                    }
                }
            }

            Collections.sort(appList, Comparator.comparing(AppInfo::getLabel));

            return appList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            adapter = new AppAdapter(getContext(),appInfos);
            appListView.setAdapter(adapter);
        }
        //Fonction filtrant les applications affichées dans la liste (applis de base + toutes les applis installées par l'utilisateur
        protected boolean filter(ApplicationInfo appInfo) {
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
    //Fonction créant la fenêtre pop-up qui permet de choisir son mode de vibration
    public void createNewVibrationModeDialog(AppInfo appInfo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
        final View vibrationModeDialog = getLayoutInflater().inflate(R.layout.vibration_popup_menu, null);

        RadioGroup vibrationModeRadioGroup = vibrationModeDialog.findViewById(R.id.vibrationModeRadioGroup);
        switch (appInfo.getVibrationMode()) {
            case "N":
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
        dialogBuilder.setView(vibrationModeDialog);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}