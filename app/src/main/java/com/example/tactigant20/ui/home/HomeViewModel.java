// Cette classe est vraisemblablement inutile

package com.example.tactigant20.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private static final String TAG_HOME_VIEW = "DebugHomeViewModel";


    public HomeViewModel() {
        Log.d("TAG_HOME_VIEW","Construction de HomeViewModel dans HomeViewModel");

        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }


    public LiveData<String> getText() {
        Log.d("TAG_HOME_VIEW","Appel de getText dans MenuViewModel");
        return mText;
    }
}