package com.example.tactigant20.ui.vibrations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VibrationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VibrationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vibrations fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}