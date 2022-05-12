package com.example.tactigant20.ui.vibrations;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VibrationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private static final String TAG_VIBRAS_VIEW = "DebugVibrasViewModel";

    public VibrationsViewModel() {
        Log.d(TAG_VIBRAS_VIEW,"Construction de VibrationsViewModel");

        mText = new MutableLiveData<>();
        mText.setValue("This is vibrations fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}