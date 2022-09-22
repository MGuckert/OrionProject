package com.example.tactigant20.ui.notifications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private static final String TAG_NOTIFS_VIEW = "debug_notifs_viewmodel";

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        Log.d(TAG_NOTIFS_VIEW,"Construction de NotificationsViewModel");

        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        Log.d(TAG_NOTIFS_VIEW,"Appel de getText dans NotificationsViewModel");
        return mText;
    }
}