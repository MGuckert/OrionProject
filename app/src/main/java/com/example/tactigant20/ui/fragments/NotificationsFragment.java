package com.example.tactigant20.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private static List<AppInfo> appList;
    private static List<AppInfo> searchedAppsList;
    private static int currentItemPosition;
    private static AppAdapter adapter;
    private ListView appListView;
    private long lastCheckedTimeStamp;
    private View root;

    public static int getCurrentItemPosition() {
        return currentItemPosition;
    }

    public static AppInfo getCurrentItem() {
        return appList.get(currentItemPosition);
    }

    public static AppAdapter getAdapter() {
        return adapter;
    }

    public static void setFromIndex(int position, AppInfo appInfo) {
        appList.set(position, appInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Override
    public void onResume() {
        super.onResume();
        File vibration_modes_data = new File(requireContext().getFilesDir(), "vibration_modes_data.txt");
        long lastModified = vibration_modes_data.lastModified();
        if (lastModified > lastCheckedTimeStamp) {
            if (adapter != null)
                adapter.notifyDataSetChanged();
            lastCheckedTimeStamp = lastModified;
            for (AppInfo app : appList) {
                if (getContext() == null) {
                    Log.e(TAG_NOTIFS, "getContext() renvoie null dans NotificationsFragment");
                } else {
                    String mode = MainActivity.getMyVibrationsTool().loadVibrationMode(app.getInfo().packageName, getContext());
                    if (mode.equals("UNKNOWN")) app.setVibrationMode("N");
                    else app.setVibrationMode(mode);
                }
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        com.example.tactigant20.databinding.FragmentNotificationsBinding binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

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


        EditText editText = root.findViewById(R.id.app_search_bar);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        editText.setInputType(editText.getInputType() & ~InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void filterListviewItems(String s) {
                searchedAppsList = new ArrayList<>();
                s = s.toLowerCase();
                int n = s.length();
                for (AppInfo app : appList) {
                    String name = app.getLabel().toLowerCase();
                    if (n < name.length() && name.substring(0,n).equals(s))
                        searchedAppsList.add(app);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null) {
                    searchedAppsList = appList;
                }
                else {
                    filterListviewItems(charSequence.toString());
                }
                appListView.setAdapter(new AppAdapter(getContext(), searchedAppsList));
                TextView noResults = root.findViewById(R.id.no_results_text);
                if (searchedAppsList.isEmpty()) {
                    Toast toast = Toast.makeText(getContext(), "Aucune application ne correspond à votre recherche.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button deleteButton = root.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> editText.setText(""));

        return root;
    }

    //Fonction créant la fenêtre pop-up qui permet de choisir son mode de vibration
    public void createNewVibrationModeDialog(AppInfo appInfo) {
        final VibrationModeDialog dialog = new VibrationModeDialog(this.getContext(), appInfo);
        dialog.show();
    }

    //Classe permettant de générer la liste des applications dans un thread auxiliaire (en arrière-plan)
    @SuppressWarnings({"StaticFieldLeak", "deprecation"})
    public class LoadAppInfoTask extends AsyncTask<Integer, Integer, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected List<AppInfo> doInBackground(Integer... params) {

            PackageManager packageManager = requireContext().getPackageManager();

            List<ApplicationInfo> infos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            File vibration_modes_data = new File(requireContext().getFilesDir(), "vibration_modes_data.txt");
            if (!vibration_modes_data.exists()) {
                try {
                    requireContext().openFileOutput("vibration_modes_data.txt", MODE_PRIVATE);
                } catch (FileNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            for (ApplicationInfo info : infos) {
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
                        if (mode.equals("UNKNOWN")) app.setVibrationMode("N");
                        else app.setVibrationMode(mode);
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
            adapter = new AppAdapter(getContext(), appInfos);
            appListView.setAdapter(adapter);
        }

        //Fonction filtrant les applications affichées dans la liste (applis de base + toutes les applis installées par l'utilisateur, sauf Orion)
        protected boolean filter(ApplicationInfo appInfo) {
            if (appInfo.packageName.equals("com.example.tactigant20"))
                return false;
            else
                return (appInfo.packageName.equals("com.google.android.apps.docs") || appInfo.packageName.equals("com.google.android.gm") || appInfo.packageName.equals("com.google.android.googlequicksearchbox") || appInfo.packageName.equals("com.google.android.calendar") || appInfo.packageName.equals("com.google.android.chrome") || appInfo.packageName.equals("com.google.android.apps.deskclock") || appInfo.packageName.equals("com.google.android.apps.maps") || appInfo.packageName.equals("com.google.android.apps.messaging") || appInfo.packageName.equals("com.android.phone") || appInfo.packageName.equals("com.google.android.apps.photos") || appInfo.packageName.equals("com.google.android.apps.youtube") || appInfo.packageName.equals("com.google.android.apps.youtube.music") || ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM));
        }
    }

}