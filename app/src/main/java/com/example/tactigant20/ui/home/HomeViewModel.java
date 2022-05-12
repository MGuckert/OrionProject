package com.example.tactigant20.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tactigant20.R;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private static final String TAG_HOME_VIEW = "DebugHomeViewModel";


    public HomeViewModel() {
        Log.d("TAG_MENU_VIEW","Construction de HomeViewModel dans HomeViewModel");

        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        Log.d("TAG_NOTIFICATIONS_VIEW","Appel de getText dans MenuViewModel");
        return mText;
    }
}