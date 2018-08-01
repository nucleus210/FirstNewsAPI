package com.example.nucle.firstnewsapi.settings;

import android.os.Build;
import androidx.preference.PreferenceDataStore;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyDataStore extends PreferenceDataStore implements android.preference.PreferenceDataStore {
    @Override
    public void putString(String key, @Nullable String value) {
        // Write the value somewhere ...
    }
    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        // Read the value from somewhere and return ...
        return key;
    }


}