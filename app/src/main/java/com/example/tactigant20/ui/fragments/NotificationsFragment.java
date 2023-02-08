package com.example.tactigant20.ui.fragments;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tactigant20.MainActivity;
import com.example.tactigant20.R;
import com.example.tactigant20.databinding.FragmentNotificationsBinding;
import com.example.tactigant20.model.AppAdapter;
import com.example.tactigant20.model.AppInfo;

import com.example.tactigant20.model.VibrationMode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe qui représente le fragment affichant la liste des applications et leur mode de vibration.
 * Cette classe permet également de mettre à jour les modes de vibration des applications.
 */
public class NotificationsFragment extends Fragment {

    private static List<AppInfo> appList;
    private static List<AppInfo> searchedAppList;
    private static AppAdapter adapter;
    private static boolean dataReset;
    private ListView appListView;

    public static AppAdapter getAdapter() {
        return adapter;
    }

    public static void setDataReset(boolean bool) {
        dataReset = bool;
    }

    /**
     * Cette méthode met à jour l'élément dans la liste appList avec le même label que le paramètre appInfo.
     *
     * @param appInfo l'élément à mettre à jour dans la liste appList
     */
    public static void updateItem(AppInfo appInfo) {
        String appLabel = appInfo.getLabel();
        int i = 0;
        if (!appList.isEmpty()) {
            while (i < appList.size() && !appList.get(i).getLabel().equals(appLabel))
                i++;
        }
        appList.set(i, appInfo);
    }


    /**
     * Fonction qui est appelée lors de la création du fragment.
     *
     * @param savedInstanceState l'état de l'instance du fragment sauvegardé
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Fonction qui est appelée lorsque le fragment est remis en avant-plan.
     * Cette fonction est utilisée pour réinitialiser les modes de vibration et mettre à jour l'affichage lorsque le bouton du même nom
     * est cliqué dans les paramètres
     */
    @Override
    public void onResume() {
        super.onResume();
        if (dataReset) {
            VibrationMode defaultVibrationMode = VibrationMode.getDefaultVibrationMode();
            for (AppInfo app : appList) {
                    app.setVibrationMode(defaultVibrationMode);
            }
            dataReset = false;
            getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Fonction qui est appelée lors de la création de la vue du fragment. Elle initialise la liste des applications et leur mode de vibration,
     * définit le onItemClickListener, et met en place les autres éléments de la vue.
     *
     * @param inflater           l'objet qui peut être utilisé pour créer la vue du fragment à partir d'un layout XML
     * @param container          le container parent de la vue à créer
     * @param savedInstanceState l'état de l'instance du fragment sauvegardé
     * @return la vue du fragment créée
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        com.example.tactigant20.databinding.FragmentNotificationsBinding binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appListView = root.findViewById(R.id.appList);
        appListView.setTextFilterEnabled(true);
        TextView noResultsFound = root.findViewById(R.id.no_results_text);
        appListView.setEmptyView(noResultsFound);
        appList = new ArrayList<>();
        LoadAppInfoTask task = new LoadAppInfoTask();
        task.execute();

        appListView.setOnItemClickListener((adapterView, view, i, l) -> {
            AppInfo currentItem = (AppInfo) appListView.getItemAtPosition(i);
            createNewVibrationModeDialog(currentItem);
            getAdapter().notifyDataSetChanged();
        });

        EditText editText = root.findViewById(R.id.app_search_bar);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        editText.setInputType(editText.getInputType() & ~InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void filterListviewItems(String s) {
                searchedAppList = new ArrayList<>();
                s = s.toLowerCase();
                int n = s.length();
                for (AppInfo app : appList) {
                    String name = app.getLabel().toLowerCase();
                    if (n < name.length() && name.substring(0, n).equals(s))
                        searchedAppList.add(app);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null) {
                    searchedAppList = appList;
                } else {
                    filterListviewItems(charSequence.toString());
                }
                adapter = new AppAdapter(getContext(), searchedAppList);
                appListView.setAdapter(adapter);
                getAdapter().notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        Button deleteButton = root.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> editText.setText(""));

        return root;
    }

    /**
     * Fonction créant une fenêtre de dialogue permettant de choisir le mode de vibration pour l'application spécifiée.
     *
     * @param appInfo l'application pour laquelle le mode de vibration doit être choisi.
     */
    public void createNewVibrationModeDialog(AppInfo appInfo) {
        final VibrationModeDialog dialog = new VibrationModeDialog(this.getContext(), appInfo);
        dialog.show();
    }

    /**
     * Classe permettant de charger la liste des applications dans un thread auxiliaire (en arrière-plan).
     * La liste est filtrée pour ne contenir que les applications de base et celles installées par l'utilisateur, à l'exception de l'application Orion.
     * Les modes de vibration pour chaque application sont chargés depuis un fichier JSON.
     */
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
            JSONObject root = MainActivity.getMyVibrationsTool().loadAppsVibrationModes(requireContext());
            for (ApplicationInfo info : infos) {
                if (filter(info)) {
                    AppInfo app = new AppInfo();
                    app.setInfo(info);
                    app.setLabel((String) info.loadLabel(packageManager));
                    String vibrationModeId = root.optString(info.packageName, "N");
                    VibrationMode vibrationMode = VibrationMode.getSavedVibrationModeFromId(getContext(),vibrationModeId);
                    app.setVibrationMode(vibrationMode);
                    appList.add(app);
                }
            }

            Collections.sort(appList, Comparator.comparing(AppInfo::getLabel));

            return appList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            searchedAppList = appList;
            adapter = new AppAdapter(getContext(), searchedAppList);
            appListView.setAdapter(adapter);
        }

        /**
         * Fonction filtrant les applications affichées dans la liste pour ne contenir que les applications de base et celles installées par l'utilisateur, à l'exception de l'application Orion.
         *
         * @param appInfo l'application à filtrer
         * @return vrai si l'application doit être incluse dans la liste, faux sinon.
         */
        protected boolean filter(ApplicationInfo appInfo) {
            Set<String> allowedApps = new HashSet<>(Arrays.asList(
                    "com.google.android.apps.docs",
                    "com.google.android.gm",
                    "com.google.android.googlequicksearchbox",
                    "com.google.android.calendar",
                    "com.google.android.chrome",
                    "com.google.android.apps.deskclock",
                    "com.google.android.apps.maps",
                    "com.google.android.apps.messaging",
                    "com.android.phone",
                    "com.google.android.apps.photos",
                    "com.google.android.apps.youtube",
                    "com.google.android.apps.youtube.music"
            ));

            if (appInfo.packageName.equals("com.example.tactigant20")) {
                return false;
            } else if (allowedApps.contains(appInfo.packageName)) {
                return true;
            } else {
                return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM);
            }
        }
    }

}